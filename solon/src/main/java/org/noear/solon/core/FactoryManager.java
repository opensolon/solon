package org.noear.solon.core;

import org.noear.solon.core.mvc.MvcFactoryDefault;

import java.util.function.BiFunction;

/**
 * 工厂管理器（后续会迁入更多的工厂管理）
 *
 * @author noear
 * @since 2.5
 */
public final class FactoryManager {
    //////////
    //
    // threadLocalFactory
    //
    private static BiFunction<Class<?>, Boolean, ThreadLocal> threadLocalFactory = (applyFor, inheritance0) -> {
        if (inheritance0) {
            return new InheritableThreadLocal<>();
        } else {
            return new ThreadLocal<>();
        }
    };

    /**
     * 配置线程状态管理工厂
     */
    public static <T> void threadLocalFactory(BiFunction<Class<?>, Boolean, ThreadLocal> function) {
        if (function != null) {
            threadLocalFactory = function;
        }
    }

    /**
     * 创建线程状态
     *
     * @param applyFor     申请应用的类
     * @param inheritance0 原始可继随性
     */
    public static <T> ThreadLocal<T> newThreadLocal(Class<?> applyFor, boolean inheritance0) {
        return threadLocalFactory.apply(applyFor, inheritance0);
    }

    //////////
    //
    // loadBalanceFactory 对接
    //
    protected static LoadBalance.Factory loadBalanceFactory = (g, s) -> null;

    /**
     * 配置负载工厂
     */
    public static void loadBalanceFactory(LoadBalance.Factory factory) {
        if (factory != null) {
            loadBalanceFactory = factory;
        }
    }

    /**
     * 创建负载
     */
    public static LoadBalance newLoadBalance(String group, String service) {
        return loadBalanceFactory.create(group, service);
    }


    //////////
    //
    // mvcFactory 对接
    //
    private static MvcFactory mvcFactory = new MvcFactoryDefault();

    public static boolean hasMvcFactory() {
        return mvcFactory != null;
    }

    public static MvcFactory mvcFactory() {
        if (mvcFactory == null) {
            throw new IllegalStateException("The 'solon.mvc' plugin is missing");
        }
        return mvcFactory;
    }

    public static void mvcFactory(MvcFactory factory) {
        if (factory != null) {
            mvcFactory = factory;
        }
    }
}
