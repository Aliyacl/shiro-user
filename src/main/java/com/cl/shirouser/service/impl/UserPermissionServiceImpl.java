package com.cl.shirouser.service.impl;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.dao.*;
import com.cl.shirouser.entity.Menu;
import com.cl.shirouser.entity.Operator;
import com.cl.shirouser.entity.Role;
import com.cl.shirouser.service.IUserPermissionService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userPermissionService")
public class UserPermissionServiceImpl implements IUserPermissionService {

    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private RoleOperatorMapper roleOperatorMapper;
    @Autowired
    private RoleDeptMapper roleDeptMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private OperatorMapper operatorMapper;

    public ServerResponse<List<Menu>> getMenuByUserId(int userId){
        List<Integer> roleIdList = userRoleMapper.getRoleByUserId(userId);
        List<Integer> menuIdList = new ArrayList<Integer>();
        for(Integer r:roleIdList){
            List<Integer> menuIds = roleMenuMapper.getMenuByRoleId(r);
            menuIdList.addAll(menuIds);
        }
        List<Menu> menuList = new ArrayList<Menu>();
        for(Integer m:menuIdList){
            Menu menu = menuMapper.selectByPrimaryKey(m);
            menuList.add(menu);
        }
        return ServerResponse.createBySuccess(menuList);
    }
    public ServerResponse<List<Operator>> getOperationByUserId(int userId){
        List<Integer> roleIdList = userRoleMapper.getRoleByUserId(userId);
        List<Integer> operationIdList = new ArrayList<Integer>();
        for(Integer r:roleIdList){
            List<Integer> operationIds = roleOperatorMapper.getOperationByRoleId(r);
            operationIdList.addAll(operationIds);
        }
        List<Operator> operationList = new ArrayList<Operator>();
        for(Integer o:operationIdList){
            Operator operator = operatorMapper.selectByPrimaryKey(o);
            operationList.add(operator);
        }
        return ServerResponse.createBySuccess(operationList);
    }

    public List<Integer> getDeptByUserId(int userId){
        List<Integer> roleIdList = userRoleMapper.getRoleByUserId(userId);
        List<Integer> deptIdList = new ArrayList<Integer>();
        for(Integer r:roleIdList){
            List<Integer> deptIds = roleDeptMapper.getDeptByRoleId(r);
            deptIdList.addAll(deptIds);
        }
        return deptIdList;
    }



}
