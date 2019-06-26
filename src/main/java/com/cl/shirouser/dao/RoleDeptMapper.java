package com.cl.shirouser.dao;

import com.cl.shirouser.entity.RoleDept;
import com.cl.shirouser.entity.RoleMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDeptMapper {
    int insert(RoleDept record);

    int insertSelective(RoleDept record);

    List<Integer> getDeptByRoleId(int roleId);

    int delete(int roleId);
}