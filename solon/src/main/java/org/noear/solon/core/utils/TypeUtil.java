package org.noear.solon.core.utils;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XParam;
import org.noear.solon.core.XContext;

import java.lang.reflect.AnnotatedElement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TypeUtil {
    public static Object change(AnnotatedElement p, Class<?> type, String key, String val, XContext ctx) {
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
            if (xd != null && XUtil.isEmpty(xd.value()) == false) {
                SimpleDateFormat fm = new SimpleDateFormat(xd.value());
                try {
                    return fm.parse(val);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        if (String[].class == (type)) {
            if (ctx == null) {
                return null;
            } else {
                return ctx.paramValues(key);
            }
        }

        throw new RuntimeException("不支持类型:" + type.getName());


    }

    public static Object change(Class<?> type, String val) {
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
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return fm.parse(val);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        throw new RuntimeException("不支持类型:" + type.getName());
    }

    public static Object do_change(Class<?> type, String val) {
        if (Integer.class == (type) || type == Integer.TYPE) {
            return Integer.parseInt(val);
        }

        if (Long.class == (type) || type == Long.TYPE) {
            return Long.parseLong(val);
        }

        if (Double.class == (type) || type == Double.TYPE) {
            return Double.parseDouble(val);
        }

        if (Float.class == (type) || type == Float.TYPE) {
            return Float.parseFloat(val);
        }

        if (Boolean.class == (type) || type == Boolean.TYPE) {
            return Boolean.parseBoolean(val);
        }

        return null;
    }
}
