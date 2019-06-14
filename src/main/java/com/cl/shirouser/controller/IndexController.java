package com.cl.shirouser.controller;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IUserPermissionService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/index")
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private IUserPermissionService userPermissionService;
    /*
  主页,根据用户可见的菜单权限动态显示菜单
    */
    @GetMapping("menus.do")
    @RequiresAuthentication
    public ServerResponse getMenus(){
        User user= (User) SecurityUtils.getSubject().getPrincipal();
        logger.info("从Token中获取的user参数为：{}",user);
        return userPermissionService.getMenuByUserId(user.getUserId());
    }

    public void download(){

    }


}
