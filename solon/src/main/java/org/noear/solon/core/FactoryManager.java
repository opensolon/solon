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
    private static final FactoryManager global = new FactoryManager();

    public static FactoryManager getGlobal() {
        return global;
    }

    //////////
    //
    // threadLocalFactory
    //
    private BiFunction<Class<?>, Boolean, ThreadLocal> threadLocalFactory = (applyFor, inheritance0) -> {
        if (inheritance0) {
            return new InheritableThreadLocal<>();
        } else {
            return new ThreadLocal<>();
        }
    };

    /**
     * 配置线程状态管理工厂
     */
    public <T> void threadLocalFactory(BiFunction<Class<?>, Boolean, ThreadLocal> factory) {
        if (factory != null) {
            threadLocalFactory = factory;
        }
    }

    /**
     * 创建线程状态
     *
     * @param applyFor     申请应用的类
     * @param inheritance0 原始可继随性
     */
    public <T> ThreadLocal<T> newThreadLocal(Class<?> applyFor, boolean inheritance0) {
        return threadLocalFactory.apply(applyFor, inheritance0);
    }

    //////////
    //
    // loadBalanceFactory 对接
    //
    protected LoadBalance.Factory loadBalanceFactory = (g, s) -> null;

    /**
     * 配置负载工厂
     */
    public void loadBalanceFactory(LoadBalance.Factory factory) {
        if (factory != null) {
            loadBalanceFactory = factory;
        }
    }

    /**
     * 创建负载
     */
    public LoadBalance newLoadBalance(String group, String service) {
        return loadBalanceFactory.create(group, service);
    }


    //////////
    //
    // mvcFactory 对接
    //
    private MvcFactory mvcFactory = new MvcFactoryDefault();

    public boolean hasMvcFactory() {
        return mvcFactory != null;
    }

    public MvcFactory mvcFactory() {
        if (mvcFactory == null) {
            throw new IllegalStateException("The 'solon.mvc' plugin is missing");
        }
        return mvcFactory;
    }

    public void mvcFactory(MvcFactory factory) {
        if (factory != null) {
            mvcFactory = factory;
        }
    }
}
