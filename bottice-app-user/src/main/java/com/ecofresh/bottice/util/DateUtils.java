package com.ecofresh.bottice.util;
/**
 * 日期都以年月日返回
 */
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import cn.hutool.core.date.DateUtil;

public interface DateUtils 
{
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	
	public static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 获取给定年月的最后一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonths(int year,int month)
	{
		String lastDayOfMonth = null;
		if(month==2){
			 Calendar c = Calendar.getInstance();
			 c.set(year, 2, 1);// year年的3月1日
			 c.add(Calendar.DAY_OF_MONTH, -1);//将3月1日往左偏移一天结果是2月的天数
			 String date = year+"-02-"+c.get(Calendar.DAY_OF_MONTH);
			 lastDayOfMonth = date;
		}else {
			Calendar cal = Calendar.getInstance();
			//设置年份
			cal.set(Calendar.YEAR,year);
			//设置月份
			cal.set(Calendar.MONTH, month-1);
			//获取某月最大天数
			int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			//设置日历中月份的最大天数
			cal.set(Calendar.DAY_OF_MONTH, lastDay);
			//格式化日期
			lastDayOfMonth = sdf.format(cal.getTime());
		}
		return lastDayOfMonth;
	}
	/**
	 * 获取给定年月的开始时间
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getFirstDayOfMonth(int year, int month) {     
        Calendar cal = Calendar.getInstance();     
        cal.set(Calendar.YEAR, year);     
        cal.set(Calendar.MONTH, month-1);  
        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));  
       return sdf.format(cal.getTime());  
    }
	
	/**
	 * 日期转Calendar
	 */
	public static Calendar dateTransformation(String str){
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(str));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return calendar;
	}
	
	/**
	 * 获取给定日期到当前日期的每一天
	 */
	public static List<Date> getNowTimeSlot(int i) {
		try {
			String startTime = getPastDate(i);
			String endTime=getNowDate();
			return findDates(sdf.parse(startTime),sdf.parse(endTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取当前年月日
	 */
	public static String getNowDate() {
		Date d = new Date();  
		String dateNowStr = sdf.format(d); 
		return dateNowStr;  
		
	}
	/** 
	    * 获取过去第几天的日期 
	    * 
	    * @param past 
	    * @return 
	    */  
	   public static String getPastDate(int past) {  
	       Calendar calendar = Calendar.getInstance();  
	       calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);  
	       Date today = calendar.getTime();  
	       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
	       String result = format.format(today);  
	       return result;  
	   }  
	   /** 
	    * 获取过去第几天的日期 
	    * 
	    * @param past 
	    * @return 
	    */  
	   public static String getPastDate() {  
		   String firstday="";
		// 获取前月的第一天
		   Calendar cale = Calendar.getInstance();
	        cale.add(Calendar.MONTH, 0);
	        cale.set(Calendar.DAY_OF_MONTH, 1);
	        firstday = sdf.format(cale.getTime()); 
	        return firstday;
	   } 
	/**
	 * 获取上周天数
	 */
	public static String getLastTimeInterval() {  
        Calendar calendar1 = Calendar.getInstance();  
        Calendar calendar2 = Calendar.getInstance();  
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;  
        int offset1 = 1 - dayOfWeek;  
        int offset2 = 7 - dayOfWeek;  
        calendar1.add(Calendar.DATE, offset1 - 7);  
        calendar2.add(Calendar.DATE, offset2 - 7);  
        String lastBeginDate = sdf.format(calendar1.getTime());  
        String lastEndDate = sdf.format(calendar2.getTime());  
        return lastBeginDate + "," + lastEndDate;  
   }
	//获取上月的开始时间
    public static String getBeginDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 2, 1);
        return sdf.format(getDayStartTime(calendar.getTime()));
    }
    //获取上月的结束时间
    public static String getEndDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 2, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 2, day);
        return sdf.format(getDayEndTime(calendar.getTime()));
    }
  //获取某个日期的开始时间
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if(null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),    calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }
  //获取某个日期的结束时间
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if(null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),    calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }
  //获取今年是哪一年
    public static Integer getNowYear() {
           Date date = new Date();
           GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
           gc.setTime(date);
           return Integer.valueOf(gc.get(1));
       }
    //获取本月是哪一月
    public static int getNowMonth() {
            Date date = new Date();
           GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
           gc.setTime(date);
           return gc.get(2) + 1;
       }
    /**
     * 返回时间戳到毫秒
     * @param str
     * @param i
     * @return
     */
    public static Long getDate(Date str,int i){//i==0为当天开始时间，i==1为当天结束时间
		Calendar cal = null;
		cal = dateTransformation(sdf.format(str));
		String date = "";
		Long retDate=(long) 0;
		try {
			if(i==0){
				DateUtil.beginOfDay(cal);
				date = sdf.format(cal.getTime());
				retDate=sdf.parse(date).getTime();
					
			}else if (i==1) {
				DateUtil.endOfDay(cal);
				date = sdf.format(cal.getTime());
				retDate=sdf.parse(date).getTime();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retDate;
	}
    /**
     * 计算两个日期之间的每一天
     * @param dBegin
     * @param dEnd
     * @return
     */
	public static List<Date> findDates(Date dBegin, Date dEnd)  
    {  
     List<Date> lDate = new ArrayList<Date>();  
     lDate.add(dBegin);  
     Calendar calBegin = Calendar.getInstance();  
     // 使用给定的 Date 设置此 Calendar 的时间  
     calBegin.setTime(dBegin);  
     Calendar calEnd = Calendar.getInstance();  
     // 使用给定的 Date 设置此 Calendar 的时间  
     calEnd.setTime(dEnd);  
     // 测试此日期是否在指定日期之后  
     while (dEnd.after(calBegin.getTime()))  
     {  
      // 根据日历的规则，为给定的日历字段添加或减去指定的时间量  
      calBegin.add(Calendar.DAY_OF_MONTH, 1);  
      lDate.add(calBegin.getTime());  
     }  
     return lDate;  
    }
	public static void main(String[] args) {
		String[] a = getLastTimeInterval().split(","); 
		try {
			List<Date> lDate = findDates(sdf.parse(a[0]), sdf.parse(a[1]));
			System.out.println(lDate.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
