package com.cl.shirouser.service.impl;

import com.cl.shirouser.common.ResponseCode;
import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.dao.DeptMapper;
import com.cl.shirouser.dao.UserMapper;
import com.cl.shirouser.dao.UserRoleMapper;
import com.cl.shirouser.entity.Dept;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IUserService;
import com.cl.shirouser.util.PasswordUtil;
import com.cl.shirouser.util.RedisUtil;
import com.cl.shirouser.vo.UserVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private DeptMapper deptMapper;

    public ServerResponse register(User user){
        user.setSalt(PasswordUtil.initSalt());
        user.setPassword(PasswordUtil.encrypt(user.getUsername(),user.getPassword(),user.getSalt()));
        user.setStatus(0);
        int rowCount = userMapper.insert(user);
        if(rowCount>0){
            return ServerResponse.createBySuccess("注册成功！",rowCount);
        }
        return ServerResponse.createByError();
    }

    public ServerResponse list(User userCondition,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<User> userList = userMapper.getUser(userCondition);
        PageInfo pageResult = new PageInfo(userList);
        List<UserVo> userVoList = new ArrayList<UserVo>();
        for(User u:userList){
            UserVo userVo = new UserVo();
            userVo.setUsername(u.getUsername());
            userVo.setEmail(u.getEmail());
            userVo.setDept(String.valueOf(u.getDeptId()));
            userVo.setStatus(u.getStatus()==1?"已激活":"未激活");
            userVoList.add(userVo);
        }
        List<Object> objectList =redisUtil.lGet("userList",0,-1);
        if(objectList!=null&&objectList.size()!=0){
            redisUtil.ldelete("userList");
        }
        redisUtil.lSet("userList",userVoList,1800);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse deptUserlist(Integer deptId){
        List<Integer> deptIds = new ArrayList<>();
        deptIds.add(deptId);
        deptIds = getChildDepts(deptId,deptIds);
        List<UserVo> userVoList = new ArrayList<>();
        for(Integer d:deptIds){
            List<UserVo> userVos = userMapper.getUserVoByDeptId(d);
            for(UserVo userVo:userVos){
                userVo.setStatus(userVo.getStatus().equals("1")?"已激活":"未激活");
            }
            userVoList.addAll(userVos);
        }
        List<Object> objectList =redisUtil.lGet("userList",0,-1);
        if(objectList!=null&&objectList.size()!=0){
            redisUtil.ldelete("userList");
        }
        redisUtil.lSet("userList",userVoList,1800);
        return ServerResponse.createBySuccess(userVoList);
    }
    private List<Integer> getChildDepts(Integer deptId,List<Integer> deptIds){
        List<Dept> childDepts = deptMapper.getChildDept(deptId);
        for(Dept dept:childDepts){
            deptIds.add(dept.getDeptId());
            getChildDepts(dept.getDeptId(),deptIds);
        }
        return deptIds;
    }

    public ServerResponse edit(User user){
        user.setPassword(null);
        user.setSalt(null);
        int rowCount = userMapper.updateByPrimaryKeySelective(user);
        if(rowCount>0){
            return ServerResponse.createBySuccess(rowCount);
        }
        return ServerResponse.createByError();
    }

    public ServerResponse setUserDept(String userIds,Integer deptId){
        List<Integer> userIdList = Splitter.on(",").splitToList(userIds).stream().map(Integer::parseInt).collect(Collectors.toList());
        int i = 0;
        for(Integer u:userIdList){
            User user = new User();
            user.setUserId(u);
            user.setDeptId(deptId);
            int rowCcount = userMapper.updateByPrimaryKeySelective(user);
            if(rowCcount>0){
                i++;
            }
        }
        if(i==userIdList.size()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Transactional
    public ServerResponse delete(String userIds){
        List<Integer> userIdList = Splitter.on(",").splitToList(userIds).stream().map(Integer::parseInt).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(userIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        for(Integer u:userIdList){
            List<Integer> roleIdList = userRoleMapper.getRoleByUserId(u);
            if(roleIdList!=null&&roleIdList.size()!=0){
                int rowCount = userRoleMapper.deleteRoleByUserId(u);
                if(rowCount<=0){
                    throw new RuntimeException();
                }
            }
        }
        int rowCount = userMapper.deleteByUserIds(userIdList);
        if(rowCount>0){
            return ServerResponse.createBySuccess(rowCount);
        }
        return ServerResponse.createByError();
    }

    public ServerResponse resetPassword(Integer userId,String password){
        User user = userMapper.selectByPrimaryKey(userId);
        user.setSalt(PasswordUtil.initSalt());
        user.setPassword(PasswordUtil.encrypt(user.getUsername(),password,user.getSalt()));
        int rowCount = userMapper.updateByPrimaryKeySelective(user);
        if(rowCount>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


    public User getUserByUserName(String username){
        User user = userMapper.getUserByUserName(username);
        return user;
    }

    public ServerResponse getUserByDeptId(Integer deptId){
        List<User> userList = userMapper.getUserByDeptId(deptId);
        return ServerResponse.createBySuccess(userList);
    }

    public ServerResponse batchInsertOrUpdate(List<UserVo> userVoList){
        int addRowCount=0,updateRowCount=0;
        for(UserVo u:userVoList){
            User user = new User();
            user.setDeptId(deptMapper.selectByDeptName(u.getDept()).getDeptId());
            user.setUsername(u.getUsername());
            user.setEmail(u.getEmail());
            if(u.getUserId()==null){
                user.setPassword("123456");
                ServerResponse serverResponse = this.register(user);
                if(serverResponse.isSuccess()){
                    addRowCount++;
                }else{
                    return  ServerResponse.createByError();
                }
            }else if(userMapper.selectByPrimaryKey(u.getUserId())!=null){
                user.setUserId(u.getUserId());
                ServerResponse serverResponse = this.edit(user);
                if(serverResponse.isSuccess()){
                    updateRowCount ++;
                }else{
                    return  ServerResponse.createByError();
                }
            }
        }
        return  ServerResponse.createBySuccess("新增"+addRowCount+"人，覆盖"+updateRowCount+"人");
    }



}
