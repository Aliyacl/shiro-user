package com.cl.shirouser.config;

import com.alibaba.fastjson.JSONArray;
import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.controller.LoginController;
import com.cl.shirouser.entity.Menu;
import com.cl.shirouser.entity.Operator;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IDeptService;
import com.cl.shirouser.service.IUserPermissionService;
import com.cl.shirouser.service.IUserService;
import com.cl.shirouser.util.JWTToken;
import com.cl.shirouser.util.JwtUtil;
import com.cl.shirouser.util.RedisUtil;
import com.cl.shirouser.vo.UserVo;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private IDeptService deptService;
    @Autowired
    private IUserPermissionService userPermissionService;

    private Logger logger = LoggerFactory.getLogger(UserRealm.class);

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
       List<String> menuPermissions = new ArrayList<>();
        List<String> operatorPermissions = new ArrayList<>();
       List<Object> objectList1 =redisUtil.lGet("menuList",0,-1);
        if(objectList1!=null&&objectList1.size()!=0){
            logger.info("menu是从redis取出的");
            String str = JSONArray.toJSONString(objectList1);
            String str1 = str.substring(1,str.length()-1);
            List<String> menuList =JSONArray.parseArray(str1,String.class);
            menuPermissions.addAll(menuList);
        }else{
            ServerResponse serverResponse1= userPermissionService.getMenuByUserId(user.getUserId());
            if(serverResponse1.isSuccess()){
                List<Menu> menuList = (List<Menu>) serverResponse1.getData();
                for(Menu m:menuList){
                    menuPermissions.add(m.getPerms());
                }
                redisUtil.lSet("menuList",menuPermissions,1800);
                logger.info("menu存入redis啦");
            }
        }
        simpleAuthorizationInfo.addStringPermissions(menuPermissions);
        List<Object> objectList2 =redisUtil.lGet("operatorList",0,-1);
        if(objectList2!=null&&objectList2.size()!=0) {
            logger.info("operator是从redis取出的");
            String str = JSONArray.toJSONString(objectList2);
            String str1 = str.substring(1, str.length() - 1);
            List<String> operatorList = JSONArray.parseArray(str1, String.class);
            operatorPermissions.addAll(operatorList);
        }else{
            ServerResponse serverResponse2= userPermissionService.getOperationByUserId(user.getUserId());
            if(serverResponse2.isSuccess()){
                List<Operator> operatorList = (List<Operator>) serverResponse2.getData();
                for(Operator o:operatorList){
                    operatorPermissions.add(o.getPerms());
                }
                redisUtil.lSet("operatorList",operatorPermissions,1800);
                logger.info("operator存入redis啦");
            }
        }
        simpleAuthorizationInfo.addStringPermissions(operatorPermissions);
        return simpleAuthorizationInfo;
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
        }else{
            List<Integer> deptIdList = userPermissionService.getDeptByUserId(user.getUserId());
            List<Integer> allDeptIds = new ArrayList<>();
            allDeptIds.addAll(deptIdList);
            for (Integer d:deptIdList){
                List<Integer> tempDeptId = new ArrayList<>();
                tempDeptId= deptService.getChildDepts(d,tempDeptId);
                allDeptIds.addAll(tempDeptId);
            }
            user.setAuthDeptIds(allDeptIds);
        }
        //验证token是否超时失效&账号密码是否正确
        if(!JwtUtil.verify(token,username,user.getPassword())){
            throw new AuthenticationException("用户超时或密码错误，请重新登录");
        }
        return new SimpleAuthenticationInfo(user,token,"userShiroRealm");
    }
}
