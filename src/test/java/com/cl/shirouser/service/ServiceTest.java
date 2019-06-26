package com.cl.shirouser.service;

import com.cl.shirouser.BaseTest;
import com.cl.shirouser.common.ServerResponse;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ServiceTest extends BaseTest {

    @Autowired
    private  IDeptService deptService;

    @Test
    public void test(){
        List<Integer> depts = new ArrayList<>();
        depts.add(8);
        ServerResponse serverResponse = deptService.list(depts);
        System.out.println(serverResponse.getData());
    }
}
