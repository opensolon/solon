package org.noear.solon.core;

import org.noear.solon.XUtil;

import java.util.Properties;
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
    //::上下文
    /**
     * Aop上下文
     */
    private static AopContext _c = new AopContext();

    /**
     * 获取Aop上下文
     */
    public static AopContext context() {
        return _c;
    }


    //::包装bean

    /**
     * 包装bean（clz），不负责注册
     */
    public static BeanWrap wrap(Class<?> clz, Object bean) {
        return  _c.wrap(clz, bean);
    }

    public static BeanWrap wrapAndPut(Class<?> clz) {
        return wrapAndPut(clz, null);
    }

    public static BeanWrap wrapAndPut(Class<?> clz, Object bean) {
        BeanWrap wrap = _c.getWrap(clz);
        if (wrap == null) {
            wrap = new BeanWrap(clz, bean);
            _c.putWrap(clz, wrap);
        }

        return wrap;
    }

    //::获取bean

    /**
     * 获取bean (key:name)
     */
    public static <T> T get(String key) {
        BeanWrap bw = _c.getWrap(key);
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

    /**
     * 是否有bean(key: name or type)
     * */
    public static boolean has(Object key){
        return _c.getWrap(key) != null;
    }

    /**
     * 异步获取bean (key)
     */
    public static void getAsyn(String key, Consumer<BeanWrap> callback) {
        _c.getWrapAsync(key, callback);
    }

    /**
     * 异步获取bean (clz)
     */
    public static void getAsyn(Class<?> clz, Consumer<BeanWrap> callback) { //FieldWrapTmp fwT,
        _c.getWrapAsync(clz, callback);
    }


    /**
     * 尝试注入（建议使用：get(clz) ）
     */
    public static <T> T inject(T bean) {
        _c.beanInject(bean);
        return bean;
    }

    /**
     * 尝试用属性注入
     */
    public static <T> T inject(T bean, Properties propS) {
        return XUtil.injectProperties(bean, propS);
    }
}