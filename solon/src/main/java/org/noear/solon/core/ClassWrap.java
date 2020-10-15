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

    //clz
    private final Class<?> clz;
    //clz.methodS
    private final List<MethodWrap> methodWraps;
    //clz.methodS
    private final Map<Method,MethodWrap> methodWrapsMap;
    //clz.fieldS
    private final List<FieldWrap> fieldWraps;
    //clz.all_fieldS
    private final Map<String, FieldWrap> fieldAllWrapsMap;

    protected ClassWrap(Class<?> clz) {
        this.clz = clz;


        //自己申明的函数
        methodWraps = new ArrayList<>();
        methodWrapsMap = new HashMap<>();

        for (Method m : clz.getDeclaredMethods()) {
            MethodWrap m1 = MethodWrap.get(m);
            methodWraps.add(m1);
            methodWrapsMap.put(m, m1);
        }

        //所有字段的包装（自己的 + 父类的）
        fieldAllWrapsMap = new ConcurrentHashMap<>();
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
        return clz;
    }

    /**
     * 获取所有字段的包装（含超类）
     * */
    public Map<String, FieldWrap> getFieldAllWraps(){
        return Collections.unmodifiableMap(fieldAllWrapsMap);
    }


    /**
     * 获取一个字段包装
     */
    public FieldWrap getFieldWrap(Field f1) {
        FieldWrap tmp = fieldAllWrapsMap.get(f1.getName());
        if (tmp == null) {
            tmp = new FieldWrap(clz, f1);
            FieldWrap l = fieldAllWrapsMap.putIfAbsent(f1.getName(), tmp);
            if (l != null) {
                tmp = l;
            }
        }
        return tmp;
    }

    /**
     * 获取类申明的字段包装
     * */
    public List<FieldWrap> getFieldWraps(){
        return Collections.unmodifiableList(fieldWraps);
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

    /**
     * 获取类申明的方法包装
     * */
    public List<MethodWrap> getMethodWraps() {
        return Collections.unmodifiableList(methodWraps);
    }

    /**
     * 新建实例
     * */
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
     * 为实例填充数据
     * */
    public void fill(Object target, Function<String, String> data, XContext ctx) {
        for (Map.Entry<String,FieldWrap> kv : fieldAllWrapsMap.entrySet()) {
            String key = kv.getKey();
            String val0 = data.apply(key);

            if (val0 != null) {
                FieldWrap fw = kv.getValue();

                //将 string 转为目标 type，并为字段赋值
                Object val = ConvertUtil.ctxTo(fw.field, fw.type, key, val0, ctx);
                fw.setValue(target, val);
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
