package com.cl.shirouser.service;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.Dept;

public interface IDeptService {

    ServerResponse list();

    ServerResponse edit(Dept dept);

    ServerResponse add(Dept dept);

    ServerResponse del(Integer deptId);
}
