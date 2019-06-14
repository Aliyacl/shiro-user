package com.cl.shirouser.service.impl;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.dao.DeptMapper;
import com.cl.shirouser.dao.UserMapper;
import com.cl.shirouser.entity.Dept;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IDeptService;
import com.cl.shirouser.util.TreeUtil;
import com.cl.shirouser.vo.UserVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("deptService")
public class DeptServiceImpl implements IDeptService {

    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private UserMapper userMapper;

    public ServerResponse list(){
        List<Dept> deptList = deptMapper.getDepts();
        List<Dept> deptTree = TreeUtil.toTree(deptList,"deptId","parentId","children",Dept.class);
        return ServerResponse.createBySuccess(deptTree);
    }

    public ServerResponse edit(Dept dept){
       int rowCount =  deptMapper.updateByPrimaryKeySelective(dept);
       if(rowCount>0){
           return ServerResponse.createBySuccess("修改部门成功");
       }
       return ServerResponse.createByError();
    }

    public ServerResponse add(Dept dept){
        int rowCount =  deptMapper.insert(dept);
        if(rowCount>0){
            return ServerResponse.createBySuccess("新建部门成功");
        }
        return ServerResponse.createByError();
    }

    public ServerResponse del(Integer deptId){
        List<Dept> childDepts = deptMapper.getChildDept(deptId);
        if(childDepts!=null&&childDepts.size()!=0){
            return ServerResponse.createByError("请先删除此部门下的子部门");
        }
        List<User> userList = userMapper.getUserByDeptId(deptId);
        if(userList!=null&&userList.size()!=0){
            return ServerResponse.createByError("请先删除此部门下的成员");
        }
        int rowCount =  deptMapper.deleteByPrimaryKey(deptId);
        if(rowCount>0){
            return ServerResponse.createBySuccess("删除部门成功");
        }
        return ServerResponse.createByError();
    }
}
