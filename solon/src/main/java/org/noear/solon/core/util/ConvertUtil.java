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
import org.noear.solon.Utils;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.core.exception.ConvertException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.VarSpec;

import java.io.File;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.*;
import java.util.*;

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
     * @param spec 目标申明
     * @param val        值
     * @param ctx        通用上下文
     */
    public static Object to(VarSpec spec, String val, Context ctx) throws ClassCastException {
        if (String.class == spec.getType() || Object.class == spec.getType()) {
            //如果是 string 返回原始值
            return val;
        }

        if (Utils.isEmpty(val)) {
            //如果是其它类型，且为空。返回 null （空不方便转其它类型）
            return null;
        }

        Object rst = null;

        //转数组
        if (rst == null && spec.getType().isArray()) {
            String[] ary = null;
            if (ctx == null) {
                ary = val.split(",");
            } else {
                ary = spec.getValues(ctx);
                if (ary == null || ary.length == 1) {
                    //todo:可能有兼容问题("?aaa=1,2&aaa=3,4,5,6"，只传第一部份时会有歧意)
                    ary = val.split(",");
                }
            }

            rst = tryToArray(ary, spec.getType());
        }

        //转集合
        if (rst == null && Collection.class.isAssignableFrom(spec.getType())) {
            String[] ary = null;
            if (ctx == null) {
                ary = val.split(",");
            } else {
                ary = spec.getValues(ctx);
                if (ary == null || ary.length == 1) {
                    //todo:可能有兼容问题("?aaa=1,2&aaa=3,4,5,6"，只传第一部份时会有歧意)
                    ary = val.split(",");
                }
            }

            Type gType = spec.getGenericType();

            if (gType instanceof ParameterizedType) {
                Type gTypeA = ((ParameterizedType) gType).getActualTypeArguments()[0];
                if (gTypeA instanceof Class) {
                    List ary2 = new ArrayList(ary.length);
                    for (int i = 0; i < ary.length; i++) {
                        ary2.add(tryTo((Class<?>) gTypeA, ary[i]));
                    }
                    rst = tryToColl(spec.getType(), ary2);
                } else {
                    rst = tryToColl(spec.getType(), Arrays.asList(ary));
                }
            } else {
                rst = tryToColl(spec.getType(), Arrays.asList(ary));
            }
        }

        //转其它（不是数组，也不是集合）
        if (rst == null) {
            rst = tryTo(spec.getType(), val);
        }

        if (rst == null) {
            throw new ClassCastException("Unsupported type:" + spec.getName());
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
        return to(type, null, val);
    }

    /**
     * 转换 properties 的值
     *
     * @param type 目标类型
     * @param val  属性值
     */
    public static Object to(Class<?> type, Type genericType, String val) throws ClassCastException {
        if (String.class == type || Object.class == type) {
            return val;
        }

        if (val.length() == 0) {
            return null;
        }


        Object rst = null;

        //转数组
        if (rst == null && type.isArray()) {
            String[] ary = val.split(",");

            rst = tryToArray(ary, type);
        }

        //转集合
        if (rst == null && Collection.class.isAssignableFrom(type)) {
            String[] ary = val.split(",");

            if (genericType instanceof ParameterizedType) {
                Type gTypeA = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                if (gTypeA instanceof Class) {
                    List ary2 = new ArrayList(ary.length);
                    for (int i = 0; i < ary.length; i++) {
                        ary2.add(tryTo((Class<?>) gTypeA, ary[i]));
                    }
                    rst = tryToColl(type, ary2);
                } else {
                    rst = tryToColl(type, Arrays.asList(ary));
                }
            } else {
                rst = tryToColl(type, Arrays.asList(ary));
            }
        }

        //转其它
        if (rst == null) {
            rst = tryTo(type, val);
        }

        if (rst == null) {
            throw new ClassCastException("Unsupported type:" + type.getName());
        } else {
            return rst;
        }
    }


    private static Object tryToColl(Class<?> type, List list) {
        if (type.isInterface()) {
            if (Set.class.equals(type)) {
                return new TreeSet<>(list);
            }

            return list;
        } else {
            try {
                Collection coll = ClassUtil.newInstance(type);
                coll.addAll(list);
                return coll;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Object tryToArray(String[] ary, Class<?> type) {
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
        //尝试获取转换器
        if (Solon.app() != null) {
            Converter converter = Solon.app().converterManager().find(String.class, type);
            if (converter != null) {
                return converter.convert(val);
            }
        }

        if (Byte.class == type || type == Byte.TYPE) {
            return Byte.parseByte(val);
        }

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
            if ("1".equals(val)) {
                return true;
            }

            return Boolean.parseBoolean(val);
        }

        if (Date.class == type) {
            return dateOf(val);
        }

        if (LocalDate.class == type) {
            //as "2007-12-03", not null
            //return LocalDate.parse(val);
            return dateOf(val).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        if (LocalTime.class == type) {
            //as "10:15:30", not null
            //return LocalTime.parse(val);
            return dateOf(val).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime();
        }

        if (LocalDateTime.class == type) {
            //as "2007-12-03T10:15:30", not null
            //return LocalDateTime.parse(val);
            return dateOf(val).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        if (Instant.class == type) {
            return dateOf(val).toInstant();
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

        if (File.class == type) {
            return new File(val);
        }

        if (Charset.class == type) {
            return Charset.forName(val);
        }

        if (Duration.class == type) {
            String tmp = val.toUpperCase();
            if (tmp.indexOf('P') != 0) {
                if (tmp.indexOf('D') > 0) {
                    tmp = "P" + tmp;
                } else {
                    tmp = "PT" + tmp;
                }
            }

            return Duration.parse(tmp);
        }

        if (String.class == type || Object.class == type) {
            return val;
        }

        return null;
    }

    private static Date dateOf(String val) {
        try {
            return DateAnalyzer.global().parse(val);
        } catch (ParseException e) {
            throw new ConvertException(e);
        }
    }

    /**
     * 获取枚举
     */
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
