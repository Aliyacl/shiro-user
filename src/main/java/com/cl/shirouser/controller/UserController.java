package com.cl.shirouser.controller;

import com.alibaba.fastjson.JSONArray;
import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.dao.UserMapper;
import com.cl.shirouser.entity.Dept;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IUserPermissionService;
import com.cl.shirouser.service.IUserService;
import com.cl.shirouser.util.DateTimeUtil;
import com.cl.shirouser.util.ExcelUtil;
import com.cl.shirouser.util.RedisUtil;
import com.cl.shirouser.vo.UserVo;
import com.google.common.base.Splitter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IUserPermissionService userPermissionService;

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    /*
   查询用户(备用接口)
   */
    @PostMapping(value = "list.do")
    @ResponseBody
    public ServerResponse list(@RequestBody Map<String,Object> params ) {
        User userCondition =new User();
        if(params.get("username")!=null){
            userCondition.setUsername((String) params.get("username"));
        }
        if(params.get("status")!=null){
            userCondition.setStatus(Integer.parseInt((String) params.get("status")));
        }
        if(params.get("deptId")!=null){
            userCondition.setDeptId(Integer.parseInt((String) params.get("deptId")));
        }
        Integer pageNum = Integer.parseInt((String) params.get("pageNum"));
        Integer pageSize = Integer.parseInt((String) params.get("pageSize"));
        logger.info("输入参数为：user：{},pageNum:{},pageSize:{}",userCondition,pageNum,pageSize);
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("user:list")){
            return userService.list(userCondition,pageNum,pageSize);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    显示部门及子部门的成员（用户）
   */
    @PostMapping(value = "dept_user_list.do")
    @ResponseBody
    public ServerResponse deptUserlist(@RequestBody Dept dept) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if(subject.isPermitted("user:list")&&user.getAuthDeptIds().contains(dept.getDeptId())){
            return userService.deptUserlist(dept.getDeptId());
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    新增用户
     */
    @PostMapping(value = "add.do")
    @ResponseBody
    public ServerResponse add(@RequestBody User user) {
        logger.info("输入参数为：user：{}",user);
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        if(subject.isPermitted("user:add")&&currentUser.getAuthDeptIds().contains(user.getDeptId())){
            return userService.register(user);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    编辑用户信息
     */
    @PostMapping(value = "edit.do")
    @ResponseBody
    public ServerResponse edit(@RequestBody User user) {
        logger.info("输入参数为：user：{}",user);
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        if(subject.isPermitted("user:update")&&currentUser.getAuthDeptIds().contains(user.getDeptId())){
            return userService.edit(user);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    设置用户所在部门(目前部门和用户是一对多)
     */
    @PostMapping(value = "set_user_dept.do")
    @ResponseBody
    public ServerResponse setUserDept(@RequestBody Map<String,String> params) {
        String userIds = params.get("userIds");
        Integer deptId = Integer.valueOf(params.get("deptId"));
        logger.info("输入参数为：userIds：{},deptId:{}",userIds,deptId);
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        if(subject.isPermitted("user:update")&&currentUser.getAuthDeptIds().contains(deptId)){
            return userService.setUserDept(userIds,deptId);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }


    /*
   删除用户
    */
    @PostMapping(value = "del.do")
    @ResponseBody
    public ServerResponse del(@RequestBody Map<String,String> params) {
        String userIds = params.get("userIds");
        logger.info("输入参数为：userIds：{}",userIds);
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        List deptIdList = new ArrayList();
        List<Integer> userIdList = Splitter.on(",").splitToList(userIds).stream().map(Integer::parseInt).collect(Collectors.toList());
        for(Integer u:userIdList){
            deptIdList.add(userMapper.selectByPrimaryKey(u).getDeptId());
        }
        if(subject.isPermitted("user:delete")&&currentUser.getAuthDeptIds().containsAll(deptIdList)){
            return userService.delete(userIds);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    重置密码
     */
    @PostMapping(value = "reset_password.do")
    @ResponseBody
    public ServerResponse resetPassword(@RequestBody Map<String,Object> params) {
        Integer userId = Integer.parseInt((String) params.get("userId"));
        String password = (String) params.get("password");
        logger.info("输入参数为：userId:{},password：{}",userId,password);
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        if(subject.isPermitted("user:reset")&&currentUser.getAuthDeptIds().contains(userMapper.selectByPrimaryKey(userId).getDeptId())){
            return userService.resetPassword(userId,password);
        }else{
            return ServerResponse.createByError("无权限");
        }
    }

    /*
    下载通讯录
    */
    @GetMapping(value = "download.do")
    public ServerResponse  download(HttpServletRequest request, HttpServletResponse response) {
            String fileName = DateTimeUtil.dateToStr(new Date());
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xls");
            try {
                response.flushBuffer();
                OutputStream outputStream = response.getOutputStream();
                List<Object> objectList =redisUtil.lGet("userList",0,-1);
                logger.info("redis取出的字符串是:{}",objectList);
                String str = JSONArray.toJSONString(objectList);
                String str1 = str.substring(1,str.length()-1);
                logger.info("当前json字符是:{}",str1);
                List<UserVo> userVoList =JSONArray.parseArray(str1,UserVo.class);
                HSSFWorkbook hssfWorkbook = ExcelUtil.createExcel(new UserVo(),userVoList);
                hssfWorkbook.write(outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

        return null;
    }

    /*
    上传通讯录
     */
    @PostMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse  upload(@RequestParam(value = "upload_file",required = false)MultipartFile multipartFile,HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("user:update")){
            try {
                InputStream is = new ByteArrayInputStream(multipartFile.getBytes());
                List<UserVo> userVoList = ExcelUtil.readExcel(is,UserVo.class);
                return userService.batchInsertOrUpdate(userVoList);
            } catch (Exception e) {
                e.printStackTrace();
                return ServerResponse.createByError();
            }
        }else{
            return ServerResponse.createByError("无权限");
        }

    }



}
