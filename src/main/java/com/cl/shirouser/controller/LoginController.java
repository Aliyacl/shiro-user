package com.cl.shirouser.controller;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IUserService;
import com.cl.shirouser.util.JwtUtil;
import com.cl.shirouser.util.PasswordUtil;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private IUserService userService;

    @PostMapping("login.do")
    public ServerResponse login(@RequestBody Map<String, String> param){
        String username =param.get("username");
        String password = param.get("password");
        logger.info("输入的参数为：username:{},password:{}",username,password);
        User user = userService.getUserByUserName(username);
        String passwordEncode = PasswordUtil.encrypt(username,password,user.getSalt());
        if(user.getPassword().equals(passwordEncode)){
            logger.info("输出的参数为：token：{}",JwtUtil.sign(username,passwordEncode));
            return ServerResponse.createBySuccess("login Success", JwtUtil.sign(username,passwordEncode));
        }else{
            throw new UnauthorizedException();
        }
    }

    @PostMapping("register.do")
    public ServerResponse register(@RequestBody User user){
        return  userService.register(user);
    }

    //@RequiresRoles注解测试方法
    @RequiresRoles("user111")
    @RequestMapping("/testRoles")
    public ServerResponse testRoles(){
        return ServerResponse.createBySuccess("你有权访问");
    }

    //异常处理测试
    public ServerResponse log(){
        return ServerResponse.createByError("你无权访问");
    }
}
