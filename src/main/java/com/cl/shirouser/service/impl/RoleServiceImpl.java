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
        List<String> roleIdLists = Splitter.on(",").splitToList(roleIds);
        List<Integer> roleIdList = roleIdLists.stream().map(Integer::parseInt).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(roleIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        for(Integer r:roleIdList){
            List<Integer> menuIds = roleMenuMapper.getMenuByRoleId(r);
            if(menuIds!=null||menuIds.size()!=0){
                String roleName = roleMapper.selectRoleNameByRoleId(r);
                return ServerResponse.createBySuccess(roleName+"还有权限，请先删除其关联的权限");
            }
        }
        int rowCount = roleMapper.deleteByRoleIds(roleIdList);
        if(rowCount>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse grantMenu(Integer userId,String menuIds){
        List<String> menuIdLists = Splitter.on(",").splitToList(menuIds);
        List<Integer> menuIdList = menuIdLists.stream().map(Integer::parseInt).collect(Collectors.toList());
        int i =0;
        for(Integer m:menuIdList){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(userId);
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
        List<String> operationIdLists = Splitter.on(",").splitToList(operationIds);
        List<Integer> operationIdList = operationIdLists.stream().map(Integer::parseInt).collect(Collectors.toList());
        int i =0;
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

    public ServerResponse retrieveMenu(Integer userId,String menuIds){
        List<String> menuIdLists = Splitter.on(",").splitToList(menuIds);
        List<Integer> menuIdList = menuIdLists.stream().map(Integer::parseInt).collect(Collectors.toList());
        int i =0;
        for(Integer m:menuIdList){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(userId);
            roleMenu.setMenuId(m);
            int rowCount = roleMenuMapper.delete(roleMenu);
            if(rowCount>0){
                i++;
            }
        }
        if(i==menuIdList.size()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
