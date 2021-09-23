package org.noear.solon.core;

import org.noear.solon.Utils;

import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Aop 管理中心，提供了手动操控Bean容器的接口
 *
 * <pre><code>
 * //手动使用模式（同步模式；因为顺序关系，Bean可能未生成）
 * UserService userService = Aop.getOrNull(UserService.class)
 *
 * //手动使用模式(异步模式；可确保Bean总会生成）
 * Aop.getAsyn(UserService.class,(bw)->{
 *     UserService userService = bw.get();
 * });
 *
 * //附：容器自动模式（会通过异步模式；可确保Bean总会生成）
 * @Inject
 * UserService userService;
 * </code></pre>
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
     * 包装bean，不注册
     *
     * @param type 类型
     * @param bean 实例
     */
    public static BeanWrap wrap(Class<?> type, Object bean) {
        return ac.wrap(type, bean);
    }

    /**
     * 包装bean，并尝试注册
     *
     * @param type 类型
     */
    public static BeanWrap wrapAndPut(Class<?> type) {
        return wrapAndPut(type, null);
    }

    /**
     * 包装bean，并尝试注册
     *
     * @param type 类型
     * @param bean 实例
     */
    public static BeanWrap wrapAndPut(Class<?> type, Object bean) {
        BeanWrap wrap = ac.getWrap(type);
        if (wrap == null) {
            wrap = new BeanWrap(type, bean);
            ac.putWrap(type, wrap);
        }

        return wrap;
    }

    //::bean获取

    /**
     * 检楂是否有bean
     *
     * @param nameOrType bean name or type
     */
    public static boolean has(Object nameOrType) {
        return ac.getWrap(nameOrType) != null;
    }


    /**
     * 获取bean
     *
     * @param name bean name
     */
    public static <T> T get(String name) {
        BeanWrap bw = ac.getWrap(name);
        return bw == null ? null : bw.get();
    }

    /**
     * 获取bean
     *
     * @param type bean type
     */
    public static <T> T get(Class<?> type) {
        BeanWrap bw = ac.getWrap(type);
        return bw == null ? null : bw.get();
//        if (type == null) {
//            return null;
//        } else {
//            return wrapAndPut(type).get();
//        }
    }

    public static <T> T getOrNew(Class<?> type) {
        return wrapAndPut(type).get();
    }

    /**
     * 获取bean
     *
     * @param type bean type
     */
//    @Deprecated
//    public static <T> T getOrNull(Class<?> type) {
//       return get(type);
//    }

    /**
     * 异步获取bean
     *
     * @param name bean name
     */
    public static void getAsyn(String name, Consumer<BeanWrap> callback) {
        ac.getWrapAsyn(name, callback);
    }

    /**
     * 异步获取bean
     *
     * @param type bean type
     */
    public static void getAsyn(Class<?> type, Consumer<BeanWrap> callback) { //FieldWrapTmp fwT,
        ac.getWrapAsyn(type, callback);
    }


    //::bean注入

    /**
     * 尝试注入
     *
     * @param bean 实例
     */
    public static <T> T inject(T bean) {
        ac.beanInject(bean);
        return bean;
    }

    /**
     * 尝试用属性注入
     *
     * @param bean  实例
     * @param propS 属性
     */
    public static <T> T inject(T bean, Properties propS) {
        return Utils.injectProperties(bean, propS);
    }


    /**
     * 添加Onloaded事件
     */
    public static void beanOnloaded(Runnable fun) {
        ac.beanOnloaded(fun);
    }

    /**
     * 遍历有name的bean包装
     *
     * @param action 执行动作
     */
    public static void beanForeach(BiConsumer<String, BeanWrap> action) {
        ac.beanForeach(action);
    }

    /**
     * 遍历没有name的bean包装
     *
     * @param action 执行动作
     */
    public static void beanForeach(Consumer<BeanWrap> action) {
        ac.beanForeach(action);
    }

    /**
     * 查找Bean
     *
     * @param filter 过滤
     */
    public static List<BeanWrap> beanFind(BiPredicate<String, BeanWrap> filter) {
        return ac.beanFind(filter);
    }

    /**
     * 查找Bean
     *
     * @param filter 过滤
     */
    public static List<BeanWrap> beanFind(Predicate<BeanWrap> filter) {
        return ac.beanFind(filter);
    }
}