package com.hl.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	private TimeUtil(){
		
	}
	public static String getCurrentTime(){
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateNowStr = sdf.format(d);
		return dateNowStr;
	}
	public static String getFileCurrentTime(){
		//用于文件命名的
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateNowStr = sdf.format(d);
		return dateNowStr;
	}
	
	public static Date getTimeDate(String date_str){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(date_str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static Timestamp StrToTimestamp(String str){
		try {
			return Timestamp.valueOf(str);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("格式转换有错误");
			return null;
		}
	}
	
	public static String TimestampToStr(Timestamp timestamp){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
        try {   
            //方法一   
            return  sdf.format(timestamp);     
        } catch (Exception e) {   
            e.printStackTrace(); 
            return null;
        }  
	}
	
	//目录为年份加月，创建文件夹用的,返回url_suffix
	public static String getYearMonthDir(){
		String str = getCurrentTime();
		String year = str.substring(0, 4);
		String month = str.substring(5, 7);
		return year + month;
	}
}
