package org.noear.solon.core;

import org.noear.solon.Solon;
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
@Deprecated
public class Aop {
    public static AopContext context() {
        return Solon.context();
    }

    //::bean包装

    /**
     * 包装bean，不注册
     *
     * @param type 类型
     * @param bean 实例
     */
    public static BeanWrap wrap(Class<?> type, Object bean) {
        return Solon.context().wrap(type, bean);
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
        return Solon.context().wrapAndPut(type, bean);
    }

    //::bean获取

    /**
     * 检楂是否有bean
     *
     * @param nameOrType bean name or type
     */
    public static boolean has(Object nameOrType) {
        return Solon.context().hasWrap(nameOrType);
    }


    /**
     * 获取bean
     *
     * @param name bean name
     */
    public static <T> T get(String name) {
        return Solon.context().getBean(name);
    }

    /**
     * 获取bean
     *
     * @param type bean type
     */
    public static <T> T get(Class<T> type) {
        return Solon.context().getBean(type);
    }

    /**
     * 获取bean或生成bean
     *
     * @param type bean type
     */
    public static <T> T getOrNew(Class<T> type) {
        return Solon.context().getBeanOrNew(type);
    }

    /**
     * 异步获取bean
     *
     * @param name bean name
     */
    public static void getAsyn(String name, Consumer<BeanWrap> callback) {
        Solon.context().getWrapAsyn(name, callback);
    }

    /**
     * 异步获取bean
     *
     * @param type bean type
     */
    public static void getAsyn(Class<?> type, Consumer<BeanWrap> callback) { //FieldWrapTmp fwT,
        Solon.context().getWrapAsyn(type, callback);
    }


    //::bean注入

    /**
     * 尝试注入
     *
     * @param bean 实例
     */
    public static <T> T inject(T bean) {
        Solon.context().beanInject(bean);
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
    public static void beanOnloaded(Consumer<AopContext> fun) {
        Solon.context().beanOnloaded(fun);
    }

    public static void beanOnloaded(int index,Consumer<AopContext> fun) {
        Solon.context().beanOnloaded(index, fun);
    }

    /**
     * 遍历有name的bean包装
     *
     * @param action 执行动作
     */
    public static void beanForeach(BiConsumer<String, BeanWrap> action) {
        Solon.context().beanForeach(action);
    }

    /**
     * 遍历没有name的bean包装
     *
     * @param action 执行动作
     */
    public static void beanForeach(Consumer<BeanWrap> action) {
        Solon.context().beanForeach(action);
    }

    /**
     * 查找Bean
     *
     * @param filter 过滤
     */
    public static List<BeanWrap> beanFind(BiPredicate<String, BeanWrap> filter) {
        return Solon.context().beanFind(filter);
    }

    /**
     * 查找Bean
     *
     * @param filter 过滤
     */
    public static List<BeanWrap> beanFind(Predicate<BeanWrap> filter) {
        return Solon.context().beanFind(filter);
    }
}