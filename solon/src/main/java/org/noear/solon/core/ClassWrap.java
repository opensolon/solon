package org.noear.solon.core;

import org.noear.solon.ext.ConvertUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class 包装，用于缓存类的方法和字段等相关信息
 *
 * @author noear
 * @since 1.0
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

    public final Class<?> clz;                        //clz
    public final List<MethodWrap> methodWraps;          //clz.methodS
    public final Map<Method,MethodWrap> methodWrapsMap;          //clz.methodS
    public final Field[] declaredFields;                        //clz.fieldS
    private final Map<String, FieldWrap> fieldWrapsMap;    //clz.all_fieldS

    protected ClassWrap(Class<?> clz) {
        this.clz = clz;

        //自己申明的字段
        declaredFields = clz.getDeclaredFields();

        //自己申明的函数
        methodWraps = new ArrayList<>();
        methodWrapsMap = new HashMap<>();

        for (Method m : clz.getDeclaredMethods()) {
            MethodWrap m1 = MethodWrap.get(m);
            methodWraps.add(m1);
            methodWrapsMap.put(m,m1);
        }

        //所有字段的包装（自己的 + 父类的）
        fieldWrapsMap = new ConcurrentHashMap<>();
        scanAllFields(clz, fieldWrapsMap::containsKey, fieldWrapsMap::put);
    }

    public Map<String, FieldWrap> fieldAll(){
        return Collections.unmodifiableMap(fieldWrapsMap);
    }


    /** 扫描一个类的所有字段（不能与Snack3的复用；它需要排除非序列化字段） */
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
        FieldWrap tmp = fieldWrapsMap.get(f1.getName());
        if (tmp == null) {
            tmp = new FieldWrap(clz, f1);
            FieldWrap l = fieldWrapsMap.putIfAbsent(f1.getName(), tmp);
            if (l != null) {
                tmp = l;
            }
        }
        return tmp;
    }

    /**
     * 获取一个方法包装
     * */
    public MethodWrap getMethodWrap(Method m1) {
        MethodWrap tmp = methodWrapsMap.get(m1);
        if (tmp == null) {
            tmp = new MethodWrap(m1);
            MethodWrap l = methodWrapsMap.putIfAbsent(m1, tmp);
            if (l != null) {
                tmp = l;
            }
        }
        return tmp;
    }

    public <T> T newBy(Function<String, String> data) {
        try {
            Object obj = clz.newInstance();

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
        for (Map.Entry<String,FieldWrap> kv : fieldWrapsMap.entrySet()) {
            String key = kv.getKey();
            String val0 = data.apply(key);

            if (val0 != null) {
                FieldWrap fw = kv.getValue();

                //将 string 转为目标 type，并为字段赋值
                Object val = ConvertUtil.changeOfCtx(fw.field, fw.type, key, val0, ctx);
                fw.setValue(target, val);
            }
        }
    }
}
