/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.util;

import org.noear.solon.Solon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具
 *
 * @author noear
 * @since 2.8
 */
public class DateUtil {
    private static final String FORMAT_29 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String FORMAT_25 = "yyyy-MM-dd'T'HH:mm:ss+HH:mm";
    private static final String FORMAT_24_ISO08601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String FORMAT_23_a = "yyyy-MM-dd HH:mm:ss,SSS";
    private static final String FORMAT_23_b = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String FORMAT_23_t = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String FORMAT_22 = "yyyyMMddHHmmssSSSZ";//z: +0000
    private static final String FORMAT_19_ISO = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String FORMAT_19_a = "yyyy-MM-dd HH:mm:ss";
    private static final String FORMAT_19_b = "yyyy/MM/dd HH:mm:ss";
    private static final String FORMAT_19_c = "yyyy.MM.dd HH:mm:ss";
    private static final String FORMAT_18 = "HH:mm:ss.SSS+HH:mm";
    private static final String FORMAT_17 = "yyyyMMddHHmmssSSS";
    private static final String FORMAT_16_a = "yyyy-MM-dd HH:mm";
    private static final String FORMAT_16_b = "yyyy/MM/dd HH:mm";
    private static final String FORMAT_16_c = "yyyy.MM.dd HH:mm";
    private static final String FORMAT_14 = "yyyyMMddHHmmss";
    private static final String FORMAT_12 = "HH:mm:ss.SSS";
    private static final String FORMAT_10_a = "yyyy-MM-dd";
    private static final String FORMAT_10_b = "yyyy/MM/dd";
    private static final String FORMAT_10_c = "yyyy.MM.dd";
    private static final String FORMAT_9 = "HH时mm分ss秒";
    private static final String FORMAT_8_a = "HH:mm:ss";
    private static final String FORMAT_8_b = "yyyyMMdd";

    /**
     * 解析
     */
    public static Date parse(String val) throws ParseException {
        if (val == null) {
            return null;
        }

        final int len = val.length();
        String ft = null;


        if (len == 29) {
            if (val.charAt(26) == ':' && val.charAt(28) == '0') {
                ft = FORMAT_29;
            }
        } else if (len == 25) {
            ft = FORMAT_25;
        } else if (len == 24) {
            if (val.charAt(10) == 'T') {
                ft = FORMAT_24_ISO08601;
            }
        } else if (len == 23) {
            if (val.charAt(10) == 'T') {
                ft = FORMAT_23_t;
            } else if (val.charAt(19) == ',') {
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
        } else if (len == 18) {
            ft = FORMAT_18;
        } else if (len == 17) {
            ft = FORMAT_17;
        } else if (len == 16) {
            char c1 = val.charAt(4);
            if (c1 == '/') {
                ft = FORMAT_16_b;
            } else if (c1 == '.') {
                ft = FORMAT_16_c;
            } else {
                ft = FORMAT_16_a;
            }
        } else if (len == 14) {
            ft = FORMAT_14;
        } else if (len == 12) {
            ft = FORMAT_12;
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
            if (Solon.app() == null) {
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

    /**
     * 转为 GMT 字符串
     */
    public static String toGmtString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }
}
