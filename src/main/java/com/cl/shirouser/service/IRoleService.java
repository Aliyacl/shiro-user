package com.cl.shirouser.service;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.Role;
import com.cl.shirouser.entity.User;

public interface IRoleService {
    ServerResponse list(Role roleCondition, int pageNum, int pageSize);

    ServerResponse add(Role role);

    ServerResponse edit(Role role);

    ServerResponse delete(String userIds);

    ServerResponse grantMenu(Integer userId,String menuIds);

    ServerResponse grantOperation(Integer roleId,String operationIds);

    ServerResponse listMenu();

    ServerResponse listOperation();

    ServerResponse retrieveMenu(Integer userId,String menuIds);



}
