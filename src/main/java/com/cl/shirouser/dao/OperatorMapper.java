package com.cl.shirouser.dao;

import com.cl.shirouser.entity.Operator;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperatorMapper {
    int deleteByPrimaryKey(Integer operatorId);

    int insert(Operator record);

    int insertSelective(Operator record);

    Operator selectByPrimaryKey(Integer operatorId);

    int updateByPrimaryKeySelective(Operator record);

    int updateByPrimaryKey(Operator record);

    List<Operator> getOperations();
}