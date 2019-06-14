package com.cl.shirouser.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyException {
    //对应处理shiro的认证异常
    public void defaultErrorHandler(HttpServletRequest request, HttpServletResponse response,Exception e) throws Exception{
        response.sendRedirect("/user/login");
    }
}
