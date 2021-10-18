package org.noear.solon.cloud.extend.aws.s3.service;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author noear
 * @since 1.3
 */

class Datetime implements Serializable,Cloneable {
    private Date _datetime;
    private Calendar _calendar = null;

    public Datetime(Date date){
        _datetime = date;
        _calendar = Calendar.getInstance();
        _calendar.setTime(date);
    }

    //当前时间
    ////@XNote("当前时间")
    public static Datetime Now(){
        return new Datetime(new Date());
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
}