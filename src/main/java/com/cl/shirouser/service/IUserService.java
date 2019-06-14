package com.cl.shirouser.service;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.vo.UserVo;

import java.util.List;

public interface IUserService {

    ServerResponse register(User user);

    ServerResponse list(User userCondition,int pageNum,int pageSize);

    ServerResponse edit(User user);

    ServerResponse delete(String userIds);

    ServerResponse resetPassword(Integer userId,String password);

    ServerResponse grantRole(Integer userId,String roleIds);

    User getUserByUserName(String username);

    ServerResponse getUserByDeptId(Integer deptId);

    ServerResponse deptUserlist(Integer deptId);

    ServerResponse setUserDept(String userIds,Integer deptId);
}
