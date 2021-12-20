package org.noear.solon.core.util;


import org.noear.solon.Solon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author noear
 * @since 1.5
 */
public class DateUtil {
    public static final String FORMAT_29 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String FORMAT_24_ISO08601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FORMAT_23_a = "yyyy-MM-dd HH:mm:ss,SSS";
    public static final String FORMAT_23_b = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_22 = "yyyyMMddHHmmssSSSZ";//z: +0000
    public static final String FORMAT_19_ISO = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FORMAT_19_a = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_19_b = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMAT_19_c = "yyyy.MM.dd HH:mm:ss";
    public static final String FORMAT_17 = "yyyyMMddHHmmssSSS";
    public static final String FORMAT_14 = "yyyyMMddHHmmss";
    public static final String FORMAT_10_a = "yyyy-MM-dd";
    public static final String FORMAT_10_b = "yyyy/MM/dd";
    public static final String FORMAT_10_c = "yyyy.MM.dd";
    public static final String FORMAT_9 = "HH时mm分ss秒";
    public static final String FORMAT_8_a = "HH:mm:ss";
    public static final String FORMAT_8_b = "yyyyMMdd";

    public static Date parse(String val) throws ParseException {
        final int len = val.length();
        String ft = null;


        if (len == 29) {
            if (val.charAt(26) == ':' && val.charAt(28) == '0') {
                ft = FORMAT_29;
            }
        } else if (len == 24) {
            if (val.charAt(10) == 'T') {
                ft = FORMAT_24_ISO08601;
            }
        } else if (len == 23) {
            if (val.charAt(19) == ',') {
                ft = FORMAT_23_a;
            } else {
                ft = FORMAT_23_b;
            }
        } else if (len == 22) {
            ft = FORMAT_22;
        } else if (len == 19) {
            if (val.charAt(10) == 'T') {
                ft = FORMAT_19_ISO;
            } else {
                char c1 = val.charAt(4);
                if (c1 == '/') {
                    ft = FORMAT_19_b;
                } else if (c1 == '.') {
                    ft = FORMAT_19_c;
                } else {
                    ft = FORMAT_19_a;
                }
            }
        } else if (len == 17) {
            ft = FORMAT_17;
        } else if (len == 14) {
            ft = FORMAT_14;
        } else if (len == 10) {
            char c1 = val.charAt(4);
            if (c1 == '/') {
                ft = FORMAT_10_b;
            } else if (c1 == '.') {
                ft = FORMAT_10_c;
            } else if (c1 == '-') {
                ft = FORMAT_10_a;
            }
        } else if (len == 9) {
            char c1 = val.charAt(4);
            if (c1 == '/') {
                ft = FORMAT_10_b; //兼容：yyyy/d/m
            } else if (c1 == '.') {
                ft = FORMAT_10_c;
            } else if (c1 == '-') {
                ft = FORMAT_10_a;
            } else {
                ft = FORMAT_9;
            }
        } else if (len == 8) {
            char c1 = val.charAt(4);
            if (c1 == '/') {
                ft = FORMAT_10_b; //兼容：yyyy/d/m
            } else if (c1 == '.') {
                ft = FORMAT_10_c;
            } else if (c1 == '-') {
                ft = FORMAT_10_a;
            } else {
                if (val.charAt(2) == ':') {
                    ft = FORMAT_8_a;
                } else {
                    ft = FORMAT_8_b;
                }
            }
        }

        if (ft != null) {
            DateFormat df = null;
            if (Solon.global() == null) {
                df = new SimpleDateFormat(ft);
            } else {
                df = new SimpleDateFormat(ft, Solon.cfg().locale());
            }

            df.setTimeZone(TimeZone.getDefault());
            return df.parse(val);
        } else {
            return null;
        }
    }
}