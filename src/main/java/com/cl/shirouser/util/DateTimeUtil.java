package com.cl.shirouser.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    public static Date longToDate(long dateTime){
        Date date = null;
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(format.format(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToStr(Date date){
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(date).replace("-","");
        return dateStr;
    }

}
