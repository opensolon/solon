package org.noear.solon.core.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
    public static final String FORMAT_19 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_10 = "yyyy-MM-dd";

    public static final String FORMAT_8 = "HH:mm:ss";

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
                ft = FORMAT_19;
            }
        } else if (len == 10) {
            ft = FORMAT_10;
        } else if (len == 8) {
            ft = FORMAT_8;
        }

        if (ft != null) {
            DateFormat df = new SimpleDateFormat(ft, Solon.cfg().locale());
            df.setTimeZone(TimeZone.getDefault());
            return df.parse(val);
        } else {
            return null;
        }
    }
}