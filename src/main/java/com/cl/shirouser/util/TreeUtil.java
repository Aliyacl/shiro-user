package com.cl.shirouser.util;

import com.cl.shirouser.exception.TreeCastException;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TreeUtil {

    public  static <T>List<T> toTree(List<T>list,String id,String pid,String children,Class<T> clazz){
        try{
            //初始化根节点集合
            List<T> roots = new ArrayList<>();

            //从当前对象或父类获取id字段
            Field idField;
            try {
                idField=clazz.getDeclaredField(id);
            } catch (NoSuchFieldException e) {
                idField=clazz.getSuperclass().getDeclaredField(id);
            }
            //从当前对象或父类获取pid字段
            Field pidField;
            try{
                pidField = clazz.getDeclaredField(pid);
            }catch (NoSuchFieldException e) {
                pidField=clazz.getSuperclass().getDeclaredField(pid);
            }
            //从当前对象或父类获取children字段
            Field childrenField;
            try{
                childrenField = clazz.getDeclaredField(children);
            }catch (NoSuchFieldException e) {
                childrenField=clazz.getSuperclass().getDeclaredField(children);
            }

            //设置取出的字段可访问
            idField.setAccessible(true);
            pidField.setAccessible(true);
            childrenField.setAccessible(true);

            //找出所有的根节点
            for(T t:list){
                Object pidObject = pidField.get(t);
                if(isRootNode(pidObject)){
                    roots.add(t);
                }
            }

            //从源集合删除所有根节点
            list.removeAll(roots);

            //遍历根节点，依次添加子节点
            for(T root:roots){
                addChild(list,root,idField,pidField,childrenField);
            }

            // 关闭字段可访问
            idField.setAccessible(false);
            pidField.setAccessible(false);
            childrenField.setAccessible(false);

            return roots;

        }catch (Exception e){
            e.printStackTrace();
            throw new TreeCastException(e);
        }

    }

    /*
    判断目标节点是否是根节点
     */
    private static boolean isRootNode(Object pid){
        boolean flag = false;
        if(pid==null){
            flag=true;
        }else if(pid instanceof String && (StringUtils.isEmpty(pid)||"0".equals(pid)||"-1".equals(pid))){
            flag=true;
        }else if(pid instanceof Integer && (Integer.valueOf(0).equals(pid)||Integer.valueOf(-1).equals(pid))){
            flag=true;
        }
        return flag;
    }

    /*
    为目标节点添加子节点
     */
    private static <T> void addChild(List<T> list,T node,Field idField,Field pidField,Field childrenField) throws IllegalAccessException {
        Object idObject = idField.get(node);
        List<T> children =(List<T>) childrenField.get(node);
        if(children==null){
            children=new ArrayList<>();
        }

        for(T t:list){
            Object pidObject = pidField.get(t);
            if(pidObject.equals(idObject)){
                //将该节点添加到目前节点的子节点集合中
                children.add(t);
                //重设目标节点的子节点集合
                childrenField.set(node,children);
                //递归添加子节点
                addChild(list,t,idField,pidField,childrenField);
            }
        }
    }

    /*
    获取某个
     */
 }
