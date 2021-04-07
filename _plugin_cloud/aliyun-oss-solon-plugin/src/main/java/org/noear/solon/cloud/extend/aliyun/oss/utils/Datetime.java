package org.noear.solon.cloud.extend.aliyun.oss.utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author noear
 * @since 1.3
 */

public class Datetime implements Serializable,Cloneable,Comparable<Datetime> {
    private Date _datetime;
    private Calendar _calendar = null;

    public Datetime(Date date){
        setFulltime(date);
    }


    ////@XNote("设置完整时间")
    public Datetime setFulltime(Date date){
        _datetime = date;
        _calendar = Calendar.getInstance();
        _calendar.setTime(date);

        return this;
    }
    //当前时间
    ////@XNote("当前时间")
    public static Datetime Now(){
        return new Datetime(new Date());
    }


    //获取计时周期数（相对于：1970.01.01 00:00:00 GMT）
    //@XNote("获取计时周期数（相对于：1970.01.01 00:00:00 GMT）")
    public long getTicks(){
        return _datetime.getTime();
    }


    @Override
    //@XNote("转为字符串")
    public String toString(){
        return toString("yyyy-MM-dd HH:mm:ss");
    }

    //@XNote("转为GMT格式字符串")
    public String toGmtString() {
        return toString("EEE, dd MMM yyyy HH:mm:ss 'GMT'",
                Locale.US,
                TimeZone.getTimeZone("GMT"));

    }

    //转成String
    //@XNote("格式化为字符串")
    public String toString(String format){
        DateFormat df = new SimpleDateFormat(format);
        return df.format(_datetime);
    }

    //转成String
    //@XNote("格式化为字符串")
    public String toString(String format, Locale locale, TimeZone timeZone) {
        DateFormat df = null;
        if (locale == null) {
            df = new SimpleDateFormat(format);
        } else {
            df = new SimpleDateFormat(format, locale);
        }

        if (timeZone != null) {
            df.setTimeZone(timeZone);
        }

        return df.format(_datetime);
    }


    @Override
    public int compareTo(Datetime anotherDatetime) {
        long thisTime = getTicks();
        long anotherTime = anotherDatetime.getTicks();
        return (thisTime<anotherTime ? -1 : (thisTime==anotherTime ? 0 : 1));
    }
}