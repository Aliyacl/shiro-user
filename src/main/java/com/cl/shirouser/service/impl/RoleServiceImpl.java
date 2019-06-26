package com.cl.shirouser.service.impl;

import com.cl.shirouser.common.ResponseCode;
import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.dao.*;
import com.cl.shirouser.entity.*;
import com.cl.shirouser.service.IRoleService;
import com.cl.shirouser.util.PasswordUtil;
import com.cl.shirouser.util.RedisUtil;
import com.cl.shirouser.vo.RoleVo;
import com.cl.shirouser.vo.UserVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.checkerframework.checker.signature.qual.BinaryNameForNonArrayInUnnamedPackage;
import org.checkerframework.checker.units.qual.A;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("roleService")
public class RoleServiceImpl implements IRoleService {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private RoleOperatorMapper roleOperatorMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private OperatorMapper operatorMapper;
    @Autowired
    private RoleDeptMapper roleDeptMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    public ServerResponse list(Role roleCondition, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Role> roleList = roleMapper.getRole(roleCondition);
        PageInfo pageResult = new PageInfo(roleList);
        List<RoleVo> roleVoList = new ArrayList<RoleVo>();
        for(Role r:roleList){
            RoleVo roleVo = new RoleVo();
            roleVo.setRoleName(r.getRoleName());
            roleVo.setRemark(r.getRemark());
            roleVoList.add(roleVo);
        }
        List<Object> objectList =redisUtil.lGet("roleList",0,-1);
        if(objectList!=null&&objectList.size()!=0){
            redisUtil.ldelete("roleList");
        }
        redisUtil.lSet("roleList",roleVoList,1800);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse add(Role role){
        int rowCount = roleMapper.insert(role);
        if(rowCount>0){
            return ServerResponse.createBySuccess("新增成功！");
        }
        return ServerResponse.createByError();
    }

    public ServerResponse edit(Role role){
        int rowCount = roleMapper.updateByPrimaryKeySelective(role);
        if(rowCount>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse delete(String roleIds){
        List<Integer> roleIdList = Splitter.on(",").splitToList(roleIds).stream().map(Integer::parseInt).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(roleIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int i =0,j=0,k=0;
        List<Integer> menuIds = new ArrayList<>();
        List<Integer> operationIds = new ArrayList<>();
        List<Integer> deptIds = new ArrayList<>();
        for(Integer r:roleIdList){
            menuIds = roleMenuMapper.getMenuByRoleId(r);
            operationIds = roleOperatorMapper.getOperationByRoleId(r);
            deptIds =  roleDeptMapper.getDeptByRoleId(r);
            if(menuIds!=null||menuIds.size()!=0){
                int rowCount =  roleMenuMapper.delete(r);
                if(rowCount>0){
                    i+=rowCount;
                }
            }
            if(operationIds!=null||operationIds.size()!=0){
                int rowCount =  roleOperatorMapper.delete(r);
                if(rowCount>0){
                    j+=rowCount;
                }
            }
            if(deptIds!=null||deptIds.size()!=0){
                int rowCount =  roleDeptMapper.delete(r);
                if(rowCount>0){
                    k+=rowCount;
                }
            }
        }
        if(menuIds.size()==i&&operationIds.size()==j&&deptIds.size()==k){
            int rowCount = roleMapper.deleteByRoleIds(roleIdList);
            if(rowCount>0){
                return ServerResponse.createBySuccess();
            }
        }
        return ServerResponse.createByError();
    }

    public ServerResponse grantMenu(Integer roleId,String menuIds){
        List<Integer> menuIdList = Splitter.on(",").splitToList(menuIds).stream().map(Integer::parseInt).collect(Collectors.toList());
        int i =0;
        List<Integer> menuIdsOld = roleMenuMapper.getMenuByRoleId(roleId);
        if(menuIdsOld!=null||menuIdsOld.size()!=0) {
            roleMenuMapper.delete(roleId);
        }
        for(Integer m:menuIdList){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(m);
            int rowCount = roleMenuMapper.insert(roleMenu);
            if(rowCount>0){
                i++;
            }
        }
        if(i==menuIdList.size()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse listMenu(){
        List<Menu> menuList = menuMapper.getMenus();
        return ServerResponse.createBySuccess(menuList);
    }

    public ServerResponse grantOperation(Integer roleId,String operationIds){
        List<Integer> operationIdList = Splitter.on(",").splitToList(operationIds).stream().map(Integer::parseInt).collect(Collectors.toList());
        int i =0;
        List<Integer> operationIdsOld = roleOperatorMapper.getOperationByRoleId(roleId);
        if(operationIdsOld!=null||operationIdsOld.size()!=0) {
            roleOperatorMapper.delete(roleId);
        }
        for(Integer o:operationIdList){
            RoleOperator roleOperator = new RoleOperator();
            roleOperator.setRoleId(roleId);
            roleOperator.setOperatorId(o);
            int rowCount = roleOperatorMapper.insert(roleOperator);
            if(rowCount>0){
                i++;
            }
        }
        if(i==operationIdList.size()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse listOperation(){
        List<Operator> operatorList = operatorMapper.getOperations();
        return ServerResponse.createBySuccess(operatorList);
    }

    public ServerResponse grantDataRange(Integer roleId,String deptIds){
        List<Integer> deptIdList =Splitter.on(",").splitToList(deptIds).stream().map(Integer::parseInt).collect(Collectors.toList());
        int i =0;
        List<Integer> deptIdsOld = roleDeptMapper.getDeptByRoleId(roleId);
        if(deptIdsOld!=null||deptIdsOld.size()!=0) {
            roleDeptMapper.delete(roleId);
        }
        for(Integer d:deptIdList){
            RoleDept roleDept = new RoleDept();
            roleDept.setRoleId(roleId);
            roleDept.setDeptId(d);
            int rowCount = roleDeptMapper.insert(roleDept);
            if(rowCount>0){
                i++;
            }
        }
        if(i==deptIdList.size()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse grantRole(Integer roleId,String userIds){
        List<Integer> userIdList = Splitter.on(",").splitToList(userIds).stream().map(Integer::parseInt).collect(Collectors.toList());
        int i =0;
        for(Integer u:userIdList){
            UserRole userRole = new UserRole();
            userRole.setUserId(u);
            userRole.setRoleId(roleId);
            int rowCount = userRoleMapper.insert(userRole);
            if(rowCount>0){
                i++;
            }
        }
        if(i==userIdList.size()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }



//    public ServerResponse retrieveMenu(Integer userId,String menuIds){
//        List<String> menuIdLists = Splitter.on(",").splitToList(menuIds);
//        List<Integer> menuIdList = menuIdLists.stream().map(Integer::parseInt).collect(Collectors.toList());
//        int i =0;
//        for(Integer m:menuIdList){
//            RoleMenu roleMenu = new RoleMenu();
//            roleMenu.setRoleId(userId);
//            roleMenu.setMenuId(m);
//            int rowCount = roleMenuMapper.delete(roleMenu);
//            if(rowCount>0){
//                i++;
//            }
//        }
//        if(i==menuIdList.size()){
//            return ServerResponse.createBySuccess();
//        }
//        return ServerResponse.createByError();
//    }

}
