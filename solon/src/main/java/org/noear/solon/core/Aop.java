package org.noear.solon.core;

import org.noear.solon.annotation.XNote;

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
     * 获取bean (key)
     */
    public static <T> T get(String key) {
        BeanWrap bw = _c.getWrap(key);
        return bw == null ? null : bw.get();
    }

    /**
     * 获取bean (clz)
     */
    public static <T> T get(Class<?> clz) {
        if (clz == null) {
            return null;
        } else {
            return wrapAndPut(clz).get();
        }
    }

    /**
     * 异步获取bean (key)
     */
    public static void getAsyn(String key, Consumer<BeanWrap> callback) {
        getAsynDo(key, callback);
    }

    /**
     * 异步获取bean (clz)
     */
    public static void getAsyn(Class<?> clz, Consumer<BeanWrap> callback) { //FieldWrapTmp fwT,
        getAsynDo(clz, callback);
    }

    private static void getAsynDo(Object key, Consumer<BeanWrap> callback) {
        BeanWrap wrap = _c.getWrap(key);

        if (wrap == null || wrap.raw() == null) {
            _c.beanSubscribe(key, callback);
        } else {
            callback.accept(wrap);
        }
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
        ClassWrap.get(bean.getClass()).fill(bean, propS::getProperty, null);
        return bean;
    }

    //::bean事件处理
    /**
     * 添加bean加载完成事件
     */
    @XNote("添加bean加载完成事件")
    public static void beanOnloaded(Runnable fun) {
        _c.loadedEvent.add(fun);
    }

    /**
     * 遍历bean库 (拿到的是bean包装)
     */
    @XNote("遍历bean库 (拿到的是bean包装)")
    public static void beanForeach(BiConsumer<String, BeanWrap> action) {
        _c.beans.forEach(action);

    }

    /**
     * 遍历bean包装库
     */
    @XNote("遍历bean包装库")
    public static void beanForeach(Consumer<BeanWrap> action) {
        _c.beanWraps.forEach((k, bw) -> {
            action.accept(bw);
        });
    }
}