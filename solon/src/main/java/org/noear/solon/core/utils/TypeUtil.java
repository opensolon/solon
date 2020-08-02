package org.noear.solon.core.utils;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XParam;
import org.noear.solon.core.XContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class TypeUtil {
    private static final SimpleDateFormat date_def_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static Object changeOfCtx(AnnotatedElement p, Class<?> type, String key, String val, XContext ctx) {
        if (String.class == (type)) {
            return val;
        }

        if (val.length() == 0) {
            return null;
        }

        Object rst = do_change(type, val);

        if (rst != null) {
            return rst;
        }

        if (Date.class == (type) && p != null) {
            XParam xd = p.getAnnotation(XParam.class);
            SimpleDateFormat format = null;

            if (xd != null && XUtil.isEmpty(xd.value()) == false) {
                format = new SimpleDateFormat(xd.value());
            }else{
                format = date_def_format;
            }

            try {
                return format.parse(val);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }

        if(type.isArray()){
            String[] ary = ctx.paramValues(key);
            if(ary == null){
                return null;
            }

            int len = ary.length;

            if (is(String[].class, type)) {
                return ary;
            } else if (is(short[].class, type)) {
                short[] ary2 = new short[len];
                for (int i = 0; i < len; i++) {
                    ary2[i] = Short.parseShort(ary[i]);
                }
                return ary2;
            } else if (is(int[].class, type) ) {
                int[] ary2 = new int[len];
                for (int i = 0; i < len; i++) {
                    ary2[i] = Integer.parseInt(ary[i]);
                }
                return ary2;
            } else if (is(long[].class, type)) {
                long[] ary2 = new long[len];
                for (int i = 0; i < len; i++) {
                    ary2[i] = Long.parseLong(ary[i]);
                }
                return ary2;
            }  else if (is(float[].class, type) ) {
                float[] ary2 = new float[len];
                for (int i = 0; i < len; i++) {
                    ary2[i] = Float.parseFloat(ary[i]);
                }
                return ary2;
            } else if (is(double[].class, type)) {
                double[] ary2 = new double[len];
                for (int i = 0; i < len; i++) {
                    ary2[i] = Double.parseDouble(ary[i]);
                }
                return ary2;
            } else if (is(Object[].class, type)) {
                Class<?> c = type.getComponentType();
                Object[] ary2 = (Object[])Array.newInstance(c,len);
                for (int i = 0; i < len; i++) {
                    ary2[i] = do_change(c, ary[i]);
                }
                return ary2;
            }
        }



        throw new RuntimeException("不支持类型:" + type.getName());
    }

    private static boolean is(Class<?> s, Class<?> t){
        return s.isAssignableFrom(t);
    }

    public static Object changeOfPop(Class<?> type, String val) {
        if (String.class == (type)) {
            return val;
        }

        if (val.length() == 0) {
            return null;
        }

        Object rst = do_change(type, val);
        if (rst != null) {
            return rst;
        }

        if (Date.class == (type)) {
            try {
                return date_def_format.parse(val);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }

        throw new RuntimeException("不支持类型:" + type.getName());
    }

    public static Object do_change(Class<?> type, String val) {
        if (Short.class == type || type == Short.TYPE) {
            return Short.parseShort(val);
        }

        if (Integer.class == type || type == Integer.TYPE) {
            return Integer.parseInt(val);
        }

        if (Long.class == type || type == Long.TYPE) {
            return Long.parseLong(val);
        }

        if (Double.class == type || type == Double.TYPE) {
            return Double.parseDouble(val);
        }

        if (Float.class == type || type == Float.TYPE) {
            return Float.parseFloat(val);
        }

        if (Boolean.class == type || type == Boolean.TYPE) {
            return Boolean.parseBoolean(val);
        }

        if(LocalDate.class == type){
            //as "2007-12-03", not null
            return LocalDate.parse(val);
        }

        if(LocalTime.class == type){
            //as "10:15:30", not null
            return LocalTime.parse(val);
        }

        if(LocalDateTime.class == type){
            //as "2007-12-03T10:15:30", not null
            return LocalDateTime.parse(val);
        }

        if(BigDecimal.class == type){
            return new BigDecimal(val);
        }

        if(BigInteger.class == type){
            return new BigInteger(val);
        }

        return null;
    }
}
