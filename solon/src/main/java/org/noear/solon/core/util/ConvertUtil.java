package org.noear.solon.core.util;

import org.noear.solon.core.handle.Context;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 类型转换工具
 *
 * @author noear
 * @since 1.0
 * */
public class ConvertUtil {

    /**
     * 转换 context 的值
     *
     * @param element 目标注解元素
     * @param type    目标类型
     * @param key     变量名
     * @param val     值
     * @param ctx     通用上下文
     */
    public static Object to(AnnotatedElement element,  Class<?> type, String key, String val, Context ctx) throws ClassCastException {
        if (String.class == (type)) {
            return val;
        }

        if (val.length() == 0) {
            return null;
        }

        Object rst = null;

        if (rst == null && Date.class == type) {
            try {
                rst = DateAnalyzer.getGlobal().parse(val);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        if (rst == null && type.isArray()) {
            String[] ary = null;
            if (ctx == null) {
                ary = val.split(",");
            } else {
                ary = ctx.paramValues(key);
                if (ary == null || ary.length == 1) {
                    //todo:可能有兼容问题("?aaa=1,2&aaa=3,4,5,6"，只传第一部份时会有歧意)
                    ary = val.split(",");
                }
            }

            rst = tryToArray(ary, type);
        }

        if (rst == null) {
            rst = tryTo(type, val);
        }

        if (rst == null) {
            throw new ClassCastException("Unsupported type:" + type.getName());
        } else {
            return rst;
        }
    }

    /**
     * 转换 properties 的值
     *
     * @param type 目标类型
     * @param val  属性值
     */
    public static Object to(Class<?> type, String val) throws ClassCastException {
        if (String.class == (type)) {
            return val;
        }

        if (val.length() == 0) {
            return null;
        }

        Object rst = tryTo(type, val);

        if (rst != null) {
            return rst;
        }

        if (Date.class == (type)) {
            try {
                return DateAnalyzer.getGlobal().parse(val);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }


        throw new ClassCastException("不支持类型:" + type.getName());
    }



    private static Object tryToArray(String[] ary, Class<?> type){
        int len = ary.length;

        if (is(String[].class, type)) {
            return ary;
        } else if (is(short[].class, type)) {
            short[] ary2 = new short[len];
            for (int i = 0; i < len; i++) {
                ary2[i] = Short.parseShort(ary[i]);
            }
            return ary2;
        } else if (is(int[].class, type)) {
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
        } else if (is(float[].class, type)) {
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
            Object[] ary2 = (Object[]) Array.newInstance(c, len);
            for (int i = 0; i < len; i++) {
                ary2[i] = tryTo(c, ary[i]);
            }
            return ary2;
        }

        return null;
    }

    /**
     * 转换 string 值
     *
     * @param type 目标类型
     * @param val  值
     */
    public static Object tryTo(Class<?> type, String val) {
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
            if("1".equals(val)){
                return true;
            }

            return Boolean.parseBoolean(val);
        }

        if (LocalDate.class == type) {
            //as "2007-12-03", not null
            return LocalDate.parse(val);
        }

        if (LocalTime.class == type) {
            //as "10:15:30", not null
            return LocalTime.parse(val);
        }

        if (LocalDateTime.class == type) {
            //as "2007-12-03T10:15:30", not null
            return LocalDateTime.parse(val);
        }

        if (BigDecimal.class == type) {
            return new BigDecimal(val);
        }

        if (BigInteger.class == type) {
            return new BigInteger(val);
        }

        if (type.isEnum()) {
            return enumOf((Class<Enum>) type, val);
        }

        return null;
    }

    /**
     * 获取枚举
     * */
    private static <T extends Enum<T>> T enumOf(Class<T> enumType, String name) {
        for (T each : enumType.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(name) == 0) {
                return each;
            }
        }
        return null;
    }

    /**
     * 检测类型是否相同
     *
     * @param s 源类型
     * @param t 目标类型
     */
    private static boolean is(Class<?> s, Class<?> t) {
        return s.isAssignableFrom(t);
    }
}
