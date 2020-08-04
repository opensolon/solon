package org.noear.solon.core;

import org.noear.solon.core.utils.TypeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

        //自己申明的字段
        fields = clz.getDeclaredFields();

        //扫描所有字段
        scanAllFields(clz, fieldWraps::containsKey, fieldWraps::put);
    }

    private Map<String, FieldWrap> fieldWraps = new ConcurrentHashMap<>();

    /** 扫描一个类的所有字段 */
    private static void scanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String,FieldWrap> consumer) {
        if (clz == null) {
            return;
        }

        for (Field f : clz.getDeclaredFields()) {
            int mod = f.getModifiers();

            if (!Modifier.isStatic(mod)) {
                f.setAccessible(true);

                if (checker.test(f.getName()) == false) {
                    consumer.accept(f.getName(), new FieldWrap(clz, f));
                }
            }
        }

        Class<?> sup = clz.getSuperclass();
        if (sup != Object.class) {
            scanAllFields(sup, checker, consumer);
        }
    }

    /**
     * 获取一个字段包装
     */
    public FieldWrap getFieldWrap(Field f1) {
        FieldWrap fw = fieldWraps.get(f1.getName());
        if (fw == null) {
            fw = new FieldWrap(clazz, f1);
            FieldWrap l = fieldWraps.putIfAbsent(f1.getName(), fw);
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
        for (Map.Entry<String,FieldWrap> kv : fieldWraps.entrySet()) {
            String key = kv.getKey();
            String val0 = data.apply(key);

            if (val0 != null) {
                FieldWrap fw = kv.getValue();

                //将 string 转为目标 type，并为字段赋值
                Object val = TypeUtil.changeOfCtx(fw.field, fw.type, key, val0, ctx);
                fw.setValue(target, val);
            }
        }
    }
}
