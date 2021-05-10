package webapp.utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;


//时间类（参考了.net 的接口）
//
// demo:: int date = Datetime.Now().addDay(-3).getDate();
//
public class Datetime implements Serializable,Cloneable,Comparable<Datetime> {
    private Date     _datetime;
    private Calendar _calendar = null;

    public Datetime(){
        setFulltime(new Date());
    }
    public Datetime(Date date){
        setFulltime(date);
    }

    public Datetime(long milliseconds){
        setFulltime(new Date(milliseconds));
    }

    ////@XNote("设置完整时间")
    public Datetime setFulltime(Date date){
        _datetime = date;
        _calendar = Calendar.getInstance();
        _calendar.setTime(date);

        return this;
    }

    ////@XNote("获取完整时间")
    public Date getFulltime(){
        return _datetime;
    }

    //当前时间
    ////@XNote("当前时间")
    public static Datetime Now(){
        return new Datetime(new Date());
    }


    //添加年
    ////@XNote("添加年数")
    public Datetime addYear(int year) {
        return doAdd(Calendar.YEAR, year);
    }

    //添加月
    //@XNote("添加月数")
    public Datetime addMonth(int month) {
        return doAdd(Calendar.MONTH, month);
    }

    //添加日
    //@XNote("添加日数")
    public Datetime addDay(int day){
        return doAdd(Calendar.DAY_OF_MONTH, day);
    }

    //添加小时
    //@XNote("添加小时数")
    public Datetime addHour(int hour){
        return doAdd(Calendar.HOUR_OF_DAY, hour);
    }

    //添加分钟
    //@XNote("添加分钟数")
    public Datetime addMinute(int minute){
        return doAdd(Calendar.MINUTE, minute);
    }

    //@XNote("添加秒数")
    public Datetime addSecond(int second) {
        return doAdd(Calendar.SECOND, second);
    }

    //@XNote("设置豪秒数")
    public Datetime addMillisecond(int millisecond) {
        return doAdd(Calendar.MILLISECOND, millisecond);
    }

    private Datetime doAdd(int field, int value){
        _calendar.add(field, value);

        _datetime = _calendar.getTime();

        return this;
    }

    ////@XNote("设置年数")
    public Datetime setYear(int year) {
        return doSet(Calendar.YEAR, year);
    }

    //添加月
    //@XNote("设置月数")
    public Datetime setMonth(int month) {
        return doSet(Calendar.MONTH, month);
    }

    //添加日
    //@XNote("设置日数")
    public Datetime setDay(int day){
        return doSet(Calendar.DAY_OF_MONTH, day);
    }

    //添加小时
    //@XNote("设置小时数")
    public Datetime setHour(int hour){
        return doSet(Calendar.HOUR_OF_DAY, hour);
    }

    //添加分钟
    //@XNote("设置分钟数")
    public Datetime setMinute(int minute){
        return doSet(Calendar.MINUTE, minute);
    }

    //@XNote("设置秒数")
    public Datetime setSecond(int second) {
        return doSet(Calendar.SECOND, second);
    }

    //@XNote("设置豪秒数")
    public Datetime setMillisecond(int millisecond) {
        return doSet(Calendar.MILLISECOND, millisecond);
    }

    private Datetime doSet(int field, int value){
        _calendar.set(field, value);

        _datetime = _calendar.getTime();

        return this;
    }

    //获取当前年份
    //@XNote("获取当前年份")
    public int getYear(){
        return _calendar.get(Calendar.YEAR);
    }

    //获取当前月份
    //@XNote("获取当前月份")
    public int getMonth(){
        return _calendar.get(Calendar.MONTH);
    }

    //获取当前日份
    //@XNote("获取当前日份")
    public int getDays(){
        return _calendar.get(Calendar.DAY_OF_MONTH);
    }

    //获取当前小时
    //@XNote("获取当前小时")
    public int getHours(){
        return _calendar.get(Calendar.HOUR_OF_DAY);
    }

    //获取当前分钟
    //@XNote("获取当前分钟")
    public int getMinutes(){
        return _calendar.get(Calendar.MINUTE);
    }

    //获取当前秒数
    //@XNote("获取当前秒数")
    public int getSeconds(){
        return _calendar.get(Calendar.SECOND);
    }

    //获取当前豪秒
    //@XNote("获取当前豪秒数")
    public long getMilliseconds(){
        return _calendar.get(Calendar.MILLISECOND);
    }

    //获取总天数（相对于：1970.01.01 00:00:00 GMT）
    //@XNote("获取总天数（相对于：1970.01.01 00:00:00 GMT）")
    public long getAllDays() {
        return getAllHours() / 24;
    }

    //获取总小时（相对于：1970.01.01 00:00:00 GMT）
    //@XNote("获取总小时（相对于：1970.01.01 00:00:00 GMT）")
    public long getAllHours(){
        return getAllMinutes()/60;
    }

    //获取总分钟（相对于：1970.01.01 00:00:00 GMT）
    //@XNote("获取总分钟（相对于：1970.01.01 00:00:00 GMT）")
    public long getAllMinutes(){
        return getAllSeconds()/ 60;
    }

    //获取总秒（相对于：1970.01.01 00:00:00 GMT）
    //@XNote("获取总秒（相对于：1970.01.01 00:00:00 GMT）")
    public long getAllSeconds(){
        return getTicks()/ 1000;
    }

    //获取总毫秒（相对于：1970.01.01 00:00:00 GMT）
    //@XNote("获取总毫秒（相对于：1970.01.01 00:00:00 GMT）")
    public long getAllMilliseconds(){
        return getTicks();
    }

    //获取计时周期数（相对于：1970.01.01 00:00:00 GMT）
    //@XNote("获取计时周期数（相对于：1970.01.01 00:00:00 GMT）")
    public long getTicks(){
        return _datetime.getTime();
    }


    //获取日期数字（yyyyMMdd）
    //@XNote("获取日期数字（yyyyMMdd）")
    public int getDate(){
        return Integer.parseInt(toString("yyyyMMdd"));
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



    //===================
    //

    //@XNote("快捷格式化时间")
    public static String format(Date date, String format){
        return new Datetime(date).toString(format);
    }

    //@XNote("根据格式解析时间")
    public static Datetime parse(String datetime, String format) throws ParseException {
        DateFormat df = new SimpleDateFormat(format);
        Date date = df.parse(datetime);
        return new Datetime(date);
    }

    //@XNote("根据多个格式解析时间")
    public static Datetime parseEx(String datetime, List<String> formats) throws Exception {
        Date date = null;

        for(String f: formats) {
            date = do_parse(datetime, f);

            if (date != null) {
                break;
            }
        }

        if(date == null) {
            throw new Exception("Unparseable date: \"" + datetime + "\"");
        }else{
            return new Datetime(date);
        }
    }

    private static Date do_parse(String source, String format){
        DateFormat df = new SimpleDateFormat(format);

        ParsePosition pos = new ParsePosition(0);
        Date result = df.parse(source, pos);
        if (pos.getIndex() == 0)
            return null;
        else
            return result;
    }

    //@XNote("尝试解析时间")
    public static Datetime tryParse(String datetime, String format)  {
        DateFormat df = new SimpleDateFormat(format);

        try {
            Date date = df.parse(datetime);
            return new Datetime(date);
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    public int compareTo(Datetime anotherDatetime) {
        long thisTime = getTicks();
        long anotherTime = anotherDatetime.getTicks();
        return (thisTime<anotherTime ? -1 : (thisTime==anotherTime ? 0 : 1));
    }
}
