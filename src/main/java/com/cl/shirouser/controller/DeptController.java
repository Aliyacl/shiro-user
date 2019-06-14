package com.cl.shirouser.controller;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.entity.Dept;
import com.cl.shirouser.service.IDeptService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private IDeptService deptService;

    /*
    显示部门树
    */
    @PostMapping(value = "list.do")
    @ResponseBody
    public ServerResponse list() {
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("dept:list")){
            return deptService.list();
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
        if(subject.isPermitted("dept:edit")){
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
        if(subject.isPermitted("dept:create")){
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
        if(subject.isPermitted("dept:del")){
            return deptService.del(dept.getDeptId());
        }else{
            return ServerResponse.createByError("无权限");
        }
    }


}
