package com.cl.shirouser.filter;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.cl.shirouser.util.JWTToken;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends BasicHttpAuthenticationFilter {
    /*
    判断用户是否想要登录，检测header里是否包含Authorization字段
     */
    protected boolean isLoginAttempAttempt(ServletRequest request, ServletResponse response){
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization!=null;
    }

    /*
    执行登录
     */
    protected boolean executeLogin(ServletRequest request,ServletResponse response){
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        JWTToken token = new JWTToken(authorization);
        //提交给realm进行登入，没有抛出异常则代表登入成功，返回true
        getSubject(request,response).login(token);
        return true;
    }

    /*
    判断是否允许访问，mappedValue是配置拦截器参数部分
     */
    protected boolean isAccessAllowed(ServletRequest request,ServletResponse response,Object mappedValue){
        if(isLoginAttempAttempt(request,response)){
            try{
                executeLogin(request,response);
            }catch (Exception e){
                response401(request,response);
            }
        }
        return true;
    }

    /*
    对跨域提供支持,CORS,比如认证服务器和应用服务器分开
     */
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /*
   将非法请求跳转到/401
    */
    private void response401(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            httpServletResponse.sendRedirect("/401");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
