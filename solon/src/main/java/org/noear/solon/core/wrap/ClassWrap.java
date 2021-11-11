package org.noear.solon.core.wrap;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.ConvertUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
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
    private static Map<Class<?>, ClassWrap> cached = new ConcurrentHashMap<>();

    /**
     * 根据clz获取一个ClassWrap
     */
    public static ClassWrap get(Class<?> clz) {
        ClassWrap cw = cached.get(clz);
        if (cw == null) {
            cw = new ClassWrap(clz);
            ClassWrap l = cached.putIfAbsent(clz, cw);
            if (l != null) {
                cw = l;
            }
        }
        return cw;
    }

    //clz //与函数同名，_开头
    private final Class<?> _clz;
    //clz.methodS
    private final Method[] methods;
    //clz.fieldS
    private final List<FieldWrap> fieldWraps;
    //clz.all_fieldS
    private final Map<String, FieldWrap> fieldAllWrapsMap;


    protected ClassWrap(Class<?> clz) {
        _clz = clz;

        //自己申明的函数
        methods = clz.getDeclaredMethods();

        //所有字段的包装（自己的 + 父类的）
        fieldAllWrapsMap = new LinkedHashMap<>();
        doScanAllFields(clz, fieldAllWrapsMap::containsKey, fieldAllWrapsMap::put);

        fieldWraps = new ArrayList<>();
        //自己申明的字段
        for (Field f : clz.getDeclaredFields()) {
            FieldWrap fw = fieldAllWrapsMap.get(f.getName());
            if (fw != null) {
                fieldWraps.add(fw);
            }
        }
    }

    public Class<?> clz() {
        return _clz;
    }

    /**
     * 获取所有字段的包装（含超类）
     * */
    public Map<String, FieldWrap> getFieldAllWraps(){
        return Collections.unmodifiableMap(fieldAllWrapsMap);
    }

    public FieldWrap getFieldWrap(String field){
        return fieldAllWrapsMap.get(field);
    }

    /**
     * 获取申明的Method
     * */
    public Method[] getMethods() {
        return methods;
    }

    /**
     * 新建实例
     *
     * @param data 填充数据
     * */
    public <T> T newBy(Function<String, String> data) {
        return newBy(data, null);
    }

    public <T> T newBy(Properties data) {
        try {
            Constructor constructor = clz().getConstructor(Properties.class);
            if (constructor != null) {
                return (T) constructor.newInstance(data);
            }
        } catch (Throwable ex) {
        }

        return newBy(data::getProperty);
    }

    /**
     * 新建实例
     *
     * @param data 填充数据
     * @param ctx 上下文
     * */
    public <T> T newBy(Function<String, String> data, Context ctx) {
        try {
            Object obj = clz().newInstance();

            fill(obj, data, ctx);

            return (T)obj;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 为实例填充数据
     *
     * @param data 填充数据
     * */
    public void fill(Object bean, Function<String, String> data) {
        fill(bean, data, null);
    }

    /**
     * 为实例填充数据
     *
     * @param data 填充数据
     * @param ctx 上下文
     * */
    public void fill(Object bean, Function<String, String> data, Context ctx) {
        for (Map.Entry<String,FieldWrap> kv : fieldAllWrapsMap.entrySet()) {
            String key = kv.getKey();
            String val0 = data.apply(key);

            if (val0 != null) {
                FieldWrap fw = kv.getValue();

                //将 string 转为目标 type，并为字段赋值
                Object val = ConvertUtil.to(fw.field, fw.type, key, val0, ctx);
                fw.setValue(bean, val);
            }
        }
    }

    /** 扫描一个类的所有字段（不能与Snack3的复用；它需要排除非序列化字段） */
    private static void doScanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String,FieldWrap> consumer) {
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
            doScanAllFields(sup, checker, consumer);
        }
    }
}
