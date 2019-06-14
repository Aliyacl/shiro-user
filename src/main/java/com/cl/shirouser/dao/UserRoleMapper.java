package com.cl.shirouser.dao;

import com.cl.shirouser.entity.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleMapper {
    int insert(UserRole record);

    int insertSelective(UserRole record);

    List<Integer> getRoleByUserId(int userId);

    int deleteRoleByUserId(int userId);
}