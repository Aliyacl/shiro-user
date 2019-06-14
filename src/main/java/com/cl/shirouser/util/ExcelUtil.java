package com.cl.shirouser.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ExcelUtil {

    public static HSSFWorkbook createExcel(Object object, List<? extends Object> objectList){
        //创建一个Excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建一个工作表
        HSSFSheet sheet = workbook.createSheet();
        //添加表头标题行
        HSSFRow row = sheet.createRow(0);
        //设置单元格样式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        //填充表头标题
        Class clazz =object.getClass();
        Field[] f = clazz.getDeclaredFields();
        HSSFCell cell = null;
        for(int i=0;i<f.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(f[i].getName());
            cell.setCellStyle(style);
        }

        for(int i=0;i<objectList.size();i++){
            Object o = objectList.get(i);
            row = sheet.createRow(i + 1);
            Class oClazz =o.getClass();
            Field[] oFields = oClazz.getDeclaredFields();
            //将内容按顺序赋给对应的列对象
            for(int j=0;j<oFields.length;j++){
                row.createCell(j).setCellValue(getter(o,oFields[j].getName()));
            }

        }
        return workbook;
    }

    private static String getter(Object obj,String att){
        try {
            Method met = obj.getClass().getMethod("get"+initStr(att));
            return String.valueOf(met.invoke(obj));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String initStr(String old){
        String str = old.substring(0,1).toUpperCase()+old.substring(1);
        return str;
    }


}
