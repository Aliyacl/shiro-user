package com.cl.shirouser.dao;

import com.cl.shirouser.entity.RoleMenu;
import com.cl.shirouser.entity.RoleOperator;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleOperatorMapper {
    int insert(RoleOperator record);

    int insertSelective(RoleOperator record);

    List<Integer> getOperationByRoleId(int roleId);

    int delete(int roleId);


}