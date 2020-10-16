package org.noear.solon.core;

import org.noear.solon.XUtil;

import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Aop 管理中心
 *
 * 负责常用对外接口
 *
 * @author noear
 * @since 1.0
 * */
public class Aop {

    private static AopContext ac = new AopContext();

    /**
     * 获取Aop上下文
     */
    public static AopContext context() {
        return ac;
    }


    //::bean包装

    /**
     * 包装bean（clz），不注册
     */
    public static BeanWrap wrap(Class<?> clz, Object bean) {
        return  ac.wrap(clz, bean);
    }

    /**
     * 包装bean（clz），并尝试注册
     * */
    public static BeanWrap wrapAndPut(Class<?> clz) {
        return wrapAndPut(clz, null);
    }

    /**
     * 包装bean（clz），并尝试注册
     * */
    public static BeanWrap wrapAndPut(Class<?> clz, Object bean) {
        BeanWrap wrap = ac.getWrap(clz);
        if (wrap == null) {
            wrap = new BeanWrap(clz, bean);
            ac.putWrap(clz, wrap);
        }

        return wrap;
    }

    //::bean获取

    /**
     * 检楂是否有bean(key: name or type)
     * */
    public static boolean has(Object key){
        return ac.getWrap(key) != null;
    }


    /**
     * 获取bean (key:name)
     */
    public static <T> T get(String key) {
        BeanWrap bw = ac.getWrap(key);
        return bw == null ? null : bw.get();
    }

    /**
     * 获取bean (key:type)
     */
    public static <T> T get(Class<?> key) {
        if (key == null) {
            return null;
        } else {
            return wrapAndPut(key).get();
        }
    }

    public static <T> T getOrNull(Class<?> key) {
        BeanWrap bw = ac.getWrap(key);
        return bw == null ? null : bw.get();
    }

    /**
     * 异步获取bean (key)
     */
    public static void getAsyn(String key, Consumer<BeanWrap> callback) {
        ac.getWrapAsyn(key, callback);
    }

    /**
     * 异步获取bean (clz)
     */
    public static void getAsyn(Class<?> clz, Consumer<BeanWrap> callback) { //FieldWrapTmp fwT,
        ac.getWrapAsyn(clz, callback);
    }


    //::bean注入

    /**
     * 尝试注入（建议使用：get(clz) ）
     */
    public static <T> T inject(T bean) {
        ac.beanInject(bean);
        return bean;
    }

    /**
     * 尝试用属性注入
     */
    public static <T> T inject(T bean, Properties propS) {
        return XUtil.injectProperties(bean, propS);
    }


    /**
     * 添加Onloaded事件
     * */
    public static void beanOnloaded(Runnable fun){
        ac.beanOnloaded(fun);
    }

    /**
     * 遍历有name的bean包装
     * */
    public static void beanForeach(BiConsumer<String, BeanWrap> action) {
        ac.beanForeach(action);
    }

    /**
     * 遍历没有name的bean包装
     * */
    public static void beanForeach(Consumer<BeanWrap> action) {
        ac.beanForeach(action);
    }
}