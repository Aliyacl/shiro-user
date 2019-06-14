package com.cl.shirouser.dao;

import com.cl.shirouser.BaseTest;
import com.cl.shirouser.vo.UserVo;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DaoTest extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test(){
        List<UserVo> userVoList = userMapper.getUserVoByDeptId(1);
        for(UserVo u:userVoList){
            System.out.println(u);
        }
    }
}
