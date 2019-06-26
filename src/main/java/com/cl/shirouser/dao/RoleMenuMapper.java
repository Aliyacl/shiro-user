package com.cl.shirouser.dao;

import com.cl.shirouser.entity.RoleMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMenuMapper {
    int insert(RoleMenu record);

    int insertSelective(RoleMenu record);

    List<Integer> getMenuByRoleId(int roleId);

    int delete(Integer roleId);
}