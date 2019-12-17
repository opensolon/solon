package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XProperties;
import org.noear.solon.ext.Act1;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;

/**
 * Aop 管理中心（aop 的设计改了10多版本了，终于满意了...）
 * */
public class Aop {
    //::工厂
    /** Aop处理工厂 */
    private static AopFactory _f = new AopFactory();
    /** 获取Aop处理工厂 */
    public static AopFactory factory(){return _f;}
    /** 设置Aop处理工厂 */
    public static void factorySet(AopFactory factory) {
        _f = factory;
    }

    //::包装bean
    /** 包装bean（clz） */
    public static BeanWrap wrap(Class<?> clz){
        return _f.wrap(clz,null);
    }

    //::添加bean
    /** 添加bean（key + wrap） */
    public static void put(String key, BeanWrap wrap) {
        _f.put(key, wrap);
    }

    //::添加bean
    /** 添加bean（key + obj） */
    public static void put(String key, Object obj) {
        _f.put(key, new BeanWrap().build(obj.getClass(), obj));
    }

    /** 添加bean（clz + obj） */
    public static BeanWrap put(Class<?> clz, Object obj){return _f.wrap(clz, obj);}

    //::获取bean
    /** 获取bean (key) */
    public static <T> T get(String key) {
        return _f.get(key).get();
    }
    /** 异步获取bean (key) */
    public static void getAsyn(String key, Act1<BeanWrap> callback) {
        BeanWrap wrap = _f.get(key);
        if (wrap == null) {
            _f.beanSubscribe(key, callback);
        } else {
            callback.run(wrap);
        }
    }
    /** 获取bean (clz) */
    public static <T> T get(Class<?> clz) {
        return _f.wrap(clz,null).get();
    }
    /** 异步获取bean (clz) */
    public static void getAsyn(Class<?> clz, Annotation[] fAnnoS, Act1<BeanWrap> callback) {
        BeanWrap wrap = _f.wrap(clz, null);
        if (wrap == null) {
            Object raw = factory().tryBuildBean(clz, fAnnoS);

            if(raw == null){
                _f.beanSubscribe(clz, callback);
            }else{
                callback.run(new BeanWrap().build(clz,raw));
            }

        } else {
            callback.run(wrap);
        }
    }

    //::获取配置
    /** 请改用 XApp.cfg() */
    @Deprecated
    public static XProperties prop(){
        return XApp.global().prop();
    }


    //::bean事件处理
    /** 加载bean */
    public static void beanLoad(Class<?> source){_f.beanLoad(source);}
    /** 添加bean加载完成事件 */
    public static void beanOnloaded(Runnable fun){_f.loadedEvent.add(fun);}
    /** 遍历bean (拿到的是bean包装) */
    public static void beanForeach(BiConsumer<String, BeanWrap> action) {
        _f.beans.forEach(action);
    }
}