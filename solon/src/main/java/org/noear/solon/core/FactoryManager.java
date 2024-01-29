package org.noear.solon.core;

import java.util.function.Function;

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
    private static Function<Boolean, ThreadLocal> threadLocalFactory = (inheritable) -> {
        if (inheritable) {
            return new InheritableThreadLocal<>();
        } else {
            return new ThreadLocal<>();
        }
    };

    /**
     * 配置线程状态管理工厂
     */
    public static <T> void threadLocalFactory(Function<Boolean, ThreadLocal> function) {
        if (function != null) {
            threadLocalFactory = function;
        }
    }

    /**
     * 创建线程状态
     */
    public static <T> ThreadLocal<T> newThreadLocal(boolean inheritable) {
        return threadLocalFactory.apply(inheritable);
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


    static MvcFactory mvcFactory;

    public static MvcFactory mvcFactory() {
        if(mvcFactory == null){
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
