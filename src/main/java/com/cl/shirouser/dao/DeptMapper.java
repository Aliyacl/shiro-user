package com.cl.shirouser.dao;

import com.cl.shirouser.entity.Dept;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptMapper {
    int deleteByPrimaryKey(Integer deptId);

    int insert(Dept record);

    int insertSelective(Dept record);

    Dept selectByPrimaryKey(Integer deptId);

    int updateByPrimaryKeySelective(Dept record);

    int updateByPrimaryKey(Dept record);

    List<Dept> getDepts(@Param("deptIds") List<Integer> deptIds);

    List<Dept> getChildDept(Integer deptId);

    Dept selectByDeptName(String deptName);
}