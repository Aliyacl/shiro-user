package com.cl.shirouser.dao;

import com.cl.shirouser.entity.Role;
import com.cl.shirouser.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper {
    int deleteByPrimaryKey(Integer roleId);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer roleId);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    List<Role> getRole(Role roleCondition);

    int deleteByRoleIds(@Param("roleIds") List<Integer> roleIds);

    String selectRoleNameByRoleId(Integer roleId);
}