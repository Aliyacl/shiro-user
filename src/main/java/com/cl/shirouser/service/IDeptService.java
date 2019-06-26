package com.cl.shirouser.service;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.Dept;

import java.util.List;

public interface IDeptService {

    ServerResponse list(List<Integer> deptIds);

    ServerResponse edit(Dept dept);

    ServerResponse add(Dept dept);

    ServerResponse del(Integer deptId);

    List<Integer> getChildDepts(Integer deptId,List<Integer> deptIds);

    ServerResponse search(String keyword);
}
