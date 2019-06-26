package com.cl.shirouser.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    /*
        创建Excel并填充内容
         */
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

     /*
    从Excel读取内容转为List
     */
     public static <T>List<T> readExcel(InputStream inputStream, Class<T> clazz){
         //实例化对象集合
         List<T> list = new ArrayList<T>();

         HSSFWorkbook workbook = null;
         try {
             //InputStream inputStream = new FileInputStream(file);
             workbook = new HSSFWorkbook(inputStream);
             inputStream.close();
         } catch (Exception e) {
             e.printStackTrace();
         }
         //循环工作表
         for(int numSheet=0;numSheet<workbook.getNumberOfSheets();numSheet++){
             HSSFSheet hssfSheet = workbook.getSheetAt(numSheet);
             if(hssfSheet==null){
                 continue;
             }

             //获取要转换对象的各个属性
             List<Field> fieldList = new ArrayList<>();
             //循环表头
             HSSFRow hssfTitle = hssfSheet.getRow(0);
             for(int cellNum=0;cellNum<hssfTitle.getLastCellNum();cellNum++) {
                 HSSFCell cell = hssfTitle.getCell(cellNum);
                 if (cell == null) {
                     continue;
                 }
                 String fieldName = cell.getStringCellValue();
                 Field field = null;
                 try {
                     field = clazz.getDeclaredField(fieldName);
                 } catch (NoSuchFieldException e) {
                     e.printStackTrace();
                 }
                 //设置取出的字段可访问
                 field.setAccessible(true);
                 fieldList.add(field);
             }

             //循环行
             for(int rowNum=1;rowNum<=hssfSheet.getLastRowNum();rowNum++){
                 HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                 if(hssfRow==null){
                     continue;
                 }
                 T t = null;
                 try {
                     t = clazz.newInstance();
                     //循环表头
                     for(int cellNum=0;cellNum<fieldList.size();cellNum++){
                         HSSFCell cell = hssfRow.getCell(cellNum);
                         if(cell==null){
                             continue;
                         }
                         //将单元的内容存入对象集合
                         //System.out.println(fieldList.get(cellNum).getType().toString());
                         if(fieldList.get(cellNum).getType().toString().equals("class java.lang.Integer")){
                             fieldList.get(cellNum).set(t,Integer.parseInt(cell.getStringCellValue()));
                         }else{
                             fieldList.get(cellNum).set(t,cell.getStringCellValue());
                         }
                     }
                     list.add(t);
                 }catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
         return list;
     }
}
