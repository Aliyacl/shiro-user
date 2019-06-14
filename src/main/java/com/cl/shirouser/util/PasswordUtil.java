package com.cl.shirouser.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.util.Random;

public class PasswordUtil {


    private static final String ALGORITHM = "PBEWithMD5AndDES";//定义使用的算法为：PBEWithMD5AndDES
    private static final int ITERATIONCOUNT = 1000;//定义迭代次数为1000次

    /*
    初始化盐
     */
    public static String initSalt(){
        byte[] salt = new byte[8];
        Random random = new Random();
        random.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }

    /*
    把字符串转化为byte[]
     */
    public static byte[] strToByte(String str){
        return  Base64.decodeBase64(str);
    }
    /*
    加密明文字符串
     */
    public static  String encrypt(String plaintext,String password,String salt){
        Cipher cipher;
        Key key = getPBEKey(password);
        byte[] encipheredData = null;
        PBEParameterSpec parameterSpec = new PBEParameterSpec(strToByte(salt),ITERATIONCOUNT);
        try {
             cipher=Cipher.getInstance(ALGORITHM);
             cipher.init(Cipher.ENCRYPT_MODE,key,parameterSpec);
             encipheredData = cipher.doFinal(plaintext.getBytes());
        } catch (Exception e) {
             e.printStackTrace();
        }
        return bytesToHexString(encipheredData);
    }

    /*
    根据PBE密码生成一把密钥
     */
    private static Key getPBEKey(String password){
        SecretKeyFactory keyFactory;
        SecretKey secretKey = null;
        try{
            keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
            secretKey = keyFactory.generateSecret(keySpec);
        }catch (Exception e){
            e.printStackTrace();
        }
        return secretKey;
    }

    /*
    将字节数组转换为十六进制字符串
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if(src==null||src.length<=0){
            return null;
        }
        for(int i=0;i<src.length;i++){
            int v = src[i]&0xFF;
            String hv = Integer.toHexString(v);
            if(hv.length()<2){
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
