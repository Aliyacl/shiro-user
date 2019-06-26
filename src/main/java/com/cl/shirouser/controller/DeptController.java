package com.cl.shirouser.controller;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.Dept;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IDeptService;
import com.cl.shirouser.service.IUserPermissionService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dept")
public class DeptController {

    private Logger logger = LoggerFactory.getLogger(DeptController.class);

    @Autowired
    private IDeptService deptService;
    @Autowired
    private IUserPermissionService userPermissionService;

    /*
    显示部门树
    */
    @PostMapping(value = "list.do")
    @ResponseBody
    public ServerResponse list() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        logger.info("user是:{}",String.valueOf(user));
        //List<Integer> deptIds = userPermissionService.getDeptByUserId(user.getUserId());
        //logger.info("deptIds是:{}",String.valueOf(deptIds));
        if(subject.isPermitted("dept:list")){
            return deptService.list(user.getAuthDeptIds());
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
     *编辑部门，包括修改部门名称
     * 必要参数deptId，deptName
     */
    @PostMapping(value = "edit.do")
    @ResponseBody
    public ServerResponse edit(@RequestBody Dept dept){
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if(subject.isPermitted("dept:update")&&user.getAuthDeptIds().contains(dept.getDeptId())){
            return deptService.edit(dept);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    添加部门，包括添加子部门和新建部门
    必要参数deptName,parentId
     */
    @PostMapping(value = "add.do")
    @ResponseBody
    public ServerResponse add(@RequestBody Dept dept){
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if(subject.isPermitted("dept:add")&&user.getAuthDeptIds().contains(dept.getParentId())){
            return deptService.add(dept);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    删除部门，要先删除子部门和部门中成员
    必要参数deptId,parentId
     */
    @PostMapping(value = "del.do")
    @ResponseBody
    public ServerResponse dept(@RequestBody Dept dept){
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if(subject.isPermitted("dept:delete")&&user.getAuthDeptIds().contains(dept.getDeptId())){
            return deptService.del(dept.getDeptId());
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    显示搜索框
     */
    @PostMapping(value = "search.do")
    @ResponseBody
    public ServerResponse search(@RequestBody String keyword){
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("dept:list")){
            return deptService.search(keyword);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }


}
