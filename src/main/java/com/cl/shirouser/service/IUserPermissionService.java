package com.cl.shirouser.service;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.Menu;
import com.cl.shirouser.entity.Operator;


import java.util.List;

public interface IUserPermissionService {
    ServerResponse<List<Menu>> getMenuByUserId(int userId);

    ServerResponse<List<Operator>> getOperationByUserId(int userId);

    List<Integer> getDeptByUserId(int userId);


}
