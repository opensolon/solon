package org.hibernate.solon.jap;

import org.hibernate.SessionFactory;
import org.noear.solon.lang.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 仓库工厂
 *
 * @author noear
 * @since 2.5
 */
public class RepositoryProxy {
    /**
     * 创建仓库代理实例
     *
     * @param sessionFactory      会话工具
     * @param repositoryInterface 仓库接口
     */
    public static @Nullable Object newProxyInstance(SessionFactory sessionFactory, Class<?> repositoryInterface) {
        InvocationHandler invocationHandler = new RepositoryInvocationHandler(sessionFactory, repositoryInterface);

        Object proxyInstance = Proxy.newProxyInstance(
                RepositoryProxy.class.getClassLoader(),
                new Class[]{repositoryInterface}, invocationHandler);

        return proxyInstance;
    }
}
