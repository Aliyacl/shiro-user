package com.cl.shirouser.controller;

import com.alibaba.fastjson.JSONArray;
import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.dao.RoleMenuMapper;
import com.cl.shirouser.entity.Role;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IRoleService;
import com.cl.shirouser.service.IUserService;
import com.cl.shirouser.util.DateTimeUtil;
import com.cl.shirouser.util.ExcelUtil;
import com.cl.shirouser.util.RedisUtil;
import com.cl.shirouser.vo.UserVo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IRoleService roleService;

    private Logger logger = LoggerFactory.getLogger(RoleController.class);

    /*
   查询角色
   */
    @PostMapping(value = "list.do")
    @ResponseBody
    public ServerResponse list(@RequestBody Map<String,Object> params ) {
        Role roleCondition =new Role();
        if(params.get("roleName")!=null){
            roleCondition.setRoleName((String) params.get("roleName"));
        }
        if(params.get("remark")!=null){
            roleCondition.setRemark((String) params.get("remark"));
        }
        Integer pageNum = Integer.parseInt((String) params.get("pageNum"));
        Integer pageSize = Integer.parseInt((String) params.get("pageSize"));
        logger.info("输入参数为：user：{},pageNum:{},pageSize:{}",roleCondition,pageNum,pageSize);
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("role:list")){
            return roleService.list(roleCondition,pageNum,pageSize);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    新增角色
     */
    @PostMapping(value = "add.do")
    @ResponseBody
    public ServerResponse list(@RequestBody Role role) {
        logger.info("输入参数为：role：{}",role);
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("role:create")){
            return roleService.add(role);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    编辑角色
     */
    @PostMapping(value = "edit.do")
    @ResponseBody
    public ServerResponse edit(@RequestBody Role role) {
        logger.info("输入参数为：role：{}",role);
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("role:edit")){
            return roleService.edit(role);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
   删除角色
    */
    @PostMapping(value = "del.do")
    @ResponseBody
    public ServerResponse del(@RequestBody Map<String,String> params) {
        String roleIds = params.get("roleIds");
        logger.info("输入参数为：roleIds：{}",roleIds);
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("role:del")){
            return roleService.delete(roleIds);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    授权菜单
     */
    @PostMapping(value = "grant_menu.do")
    @ResponseBody
    public ServerResponse grantMenu(@RequestBody Map<String,String> params) {
        Integer roleId = Integer.valueOf(params.get("roleId"));
        String menuIds = params.get("menuIds");
        logger.info("输入参数为：roleId:{},menuIds:{}",roleId,menuIds);
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("role:list")){
            return roleService.grantMenu(roleId,menuIds);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    授权操作
     */
    @PostMapping(value = "grant_operation.do")
    @ResponseBody
    public ServerResponse grantOperation(@RequestBody Map<String,String> params) {
        Integer roleId = Integer.valueOf(params.get("roleId"));
        String operationIds = params.get("operationIds");
        logger.info("输入参数为：roleId:{},operationIds:{}",roleId,operationIds);
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("role:list")){
            return roleService.grantOperation(roleId,operationIds);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }


    /*
    显示菜单
     */
    @GetMapping(value = "list_menu.do")
    @ResponseBody
    public ServerResponse listMenu() {
        return roleService.listMenu();
    }

    /*
    显示操作
     */
    @GetMapping(value = "list_operation.do")
    @ResponseBody
    public ServerResponse listOperation() {
        return roleService.listOperation();
    }

    /*
    收回角色下的权限
     */
    @PostMapping(value = "retrieve_menu.do")
    @ResponseBody
    public ServerResponse retrieveMenu(@RequestBody Map<String,String> params) {
        Integer roleId = Integer.valueOf(params.get("roleId"));
        String menuIds = params.get("menuIds");
        logger.info("输入参数为：roleId:{},menuIds:{}",roleId,menuIds);
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("role:list")){
            return roleService.retrieveMenu(roleId,menuIds);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

}
