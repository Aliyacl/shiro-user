package com.cl.shirouser.util;

import org.apache.shiro.authc.AuthenticationToken;

public class JWTToken implements AuthenticationToken {

    // 密钥
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    /*
    获取账号
     */
    @Override
    public Object getPrincipal() {
        return token;
    }

    /*
    获取提交的凭证（token)
     */
    @Override
    public Object getCredentials() {
        return token;
    }
}
