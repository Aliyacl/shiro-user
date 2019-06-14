package com.cl.shirouser.util;

import com.alibaba.fastjson.JSONObject;
import com.cl.shirouser.BaseTest;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IUserService;
import com.cl.shirouser.vo.UserVo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class UtilTest extends BaseTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisUtil redisUtil;

//    @Test
//    public void excelTest(){
//        HSSFWorkbook hssfWorkbook = ExcelUtil.createExcel(new User(), Collections.singletonList(userService.getUserList()));
//        System.out.println(hssfWorkbook);
//    }

    @Test
    public void redisTest(){
        redisUtil.set("name","王二");
        System.out.println(redisUtil.get("name"));
        Map<String,Object> map=new HashMap<>();
        map.put("name", "张三");
        map.put("age", 24);
        map.put("address", "塞尔维亚666");
        redisUtil.hmset("15532002725", map,1000);
        System.out.println(redisUtil.hmget("15532002725"));
    }

    @Test
    public void jsonTest(){
        String str="[{\"username\":\"chenlu\",\"email\":\"961750498@qq.com\",\"dept\":\"null\",\"status\":\"未激活\"}]";
        System.out.println(str);
        List<UserVo> userVoList = JSONObject.parseArray(str, UserVo.class);
        System.out.println(userVoList);
    }

    @Test
    public void dateTest(){
        System.out.println(new Date());
        String date = DateTimeUtil.dateToStr(new Date());
        System.out.println("今天是："+date);
    }
}
