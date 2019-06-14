package com.cl.shirouser.service.impl;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.dao.MenuMapper;
import com.cl.shirouser.dao.RoleMapper;
import com.cl.shirouser.dao.RoleMenuMapper;
import com.cl.shirouser.dao.UserRoleMapper;
import com.cl.shirouser.entity.Menu;
import com.cl.shirouser.entity.Role;
import com.cl.shirouser.service.IUserPermissionService;
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
    private MenuMapper menuMapper;

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



}
