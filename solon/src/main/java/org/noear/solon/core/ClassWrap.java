package org.noear.solon.core;

import org.noear.solon.core.utils.TypeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Class 包装
 *
 *  用于缓存类的方法
 * */
public class ClassWrap {
    private static Map<Class<?>, ClassWrap> _cache = new ConcurrentHashMap<>();

    /**
     * 根据clz获取一个ClassWrap
     */
    public static ClassWrap get(Class<?> clz) {
        ClassWrap cw = _cache.get(clz);
        if (cw == null) {
            cw = new ClassWrap(clz);
            ClassWrap l = _cache.putIfAbsent(clz, cw);
            if (l != null) {
                cw = l;
            }
        }
        return cw;
    }

    public final Class<?> clazz;                //clz
    public final List<MethodWrap> methodWraps;  //clz.methodS
    public final Field[] fields;                //clz.fieldS

    protected ClassWrap(Class<?> clz) {
        clazz = clz;
        methodWraps = new ArrayList<>();

        for (Method m : clz.getDeclaredMethods()) {
            methodWraps.add(MethodWrap.get(m));
        }

        fields = clz.getDeclaredFields();
    }

    private Map<String, FieldWrap> _fwS = new ConcurrentHashMap<>();

    /**
     * 获取一个字段包装
     */
    public FieldWrap getFieldWrap(Field f1) {
        FieldWrap fw = _fwS.get(f1.getName());
        if (fw == null) {
            fw = new FieldWrap(clazz, f1);
            FieldWrap l = _fwS.putIfAbsent(f1.getName(), fw);
            if (l != null) {
                fw = l;
            }
        }
        return fw;
    }

    public <T> T newBy(Function<String, String> data) {
        try {
            Object obj = clazz.newInstance();

            fill(obj, data, null);

            return (T)obj;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 为一个对象填充数据
     * */
    public void fill(Object target, Function<String, String> data, XContext ctx) {
        for (Field f : fields) {
            String key = f.getName();
            String val0 = data.apply(key);

            if (val0 != null) {
                //将 string 转为目标 type，并为字段赋值
                Object val = TypeUtil.changeOfCtx(f, f.getType(), key, val0, ctx);
                getFieldWrap(f).setValue(target, val);
            }
        }
    }
}
