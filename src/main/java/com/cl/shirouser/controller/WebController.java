package com.cl.shirouser.controller;

import com.alibaba.fastjson.JSONArray;
import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.service.IUserService;
import com.cl.shirouser.util.DateTimeUtil;
import com.cl.shirouser.util.ExcelUtil;
import com.cl.shirouser.util.RedisUtil;
import com.cl.shirouser.vo.UserVo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/download")
public class WebController {

//    @Autowired
//    private RedisUtil redisUtil;
//
//    /*
//   下载user表
//    */
//    @GetMapping(value = "download.do")
//    public ServerResponse  download(HttpServletRequest request, HttpServletResponse response) {
//        String fileName = DateTimeUtil.dateToStr(new Date());
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xls");
//        try {
//            response.flushBuffer();
//            OutputStream outputStream = response.getOutputStream();
//            List<Object> objectList =redisUtil.lGet("userList",0,-1);
//            if(objectList==null||objectList.size()==0){
//                return ServerResponse.createByError("当前操作已过期，请查询后下载");
//            }
//            String str = JSONArray.toJSONString(objectList);
//            String str1 = str.substring(1,str.length()-1);
//            List<UserVo> userVoList =JSONArray.parseArray(str1,UserVo.class);
//            HSSFWorkbook hssfWorkbook = ExcelUtil.createExcel(new UserVo(),userVoList);
//            hssfWorkbook.write(outputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
