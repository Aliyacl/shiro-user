package com.cl.shirouser.config;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.Menu;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IUserPermissionService;
import com.cl.shirouser.service.IUserService;
import com.cl.shirouser.util.JWTToken;
import com.cl.shirouser.util.JwtUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserPermissionService userPermissionService;

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
       User user = (User) principalCollection.getPrimaryPrincipal();
       SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
       ServerResponse serverResponse= userPermissionService.getMenuByUserId(user.getUserId());
       if(serverResponse.isSuccess()){
           List<Menu> menuList = (List<Menu>) serverResponse.getData();
           List<String> permissions = new ArrayList<>();
           for(Menu m:menuList){
               permissions.add(m.getPerms());
           }
           simpleAuthorizationInfo.addStringPermissions(permissions);
           return simpleAuthorizationInfo;
       }
        return null;
    }

    /*
    获取身份信息，从token验证用户名正确与否
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        //从token中获取username
        String username = JwtUtil.getUserName(token);
        if(username==null){
            throw new AuthenticationException("token非法无效");
        }
        //查询用户信息
        User user = userService.getUserByUserName(username);
        if(user==null){
            throw new AuthenticationException("用户不存在");
        }
        //验证token是否超时失效&账号密码是否正确
        if(!JwtUtil.verify(token,username,user.getPassword())){
            throw new AuthenticationException("用户超时或密码错误，请重新登录");
        }
        return new SimpleAuthenticationInfo(user,token,"userShiroRealm");
    }
}
