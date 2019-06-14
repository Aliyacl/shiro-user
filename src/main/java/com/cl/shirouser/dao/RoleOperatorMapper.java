package com.cl.shirouser.dao;

import com.cl.shirouser.entity.RoleOperator;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleOperatorMapper {
    int insert(RoleOperator record);

    int insertSelective(RoleOperator record);
}