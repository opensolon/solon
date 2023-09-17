package org.hibernate.solon;

import org.hibernate.SessionFactory;

import java.lang.reflect.InvocationHandler;

/**
 * BaseMapper 定义
 *
 * @author noear
 * @since 2.5
 */
public interface BaseMapperDefinition {
    /**
     * 获取 Mapper 类
     * */
    Class<?> getMapperClz();

    /**
     * 获取调用代理
     * */
    InvocationHandler getInvocationHandler(SessionFactory sessionFactory, Class<?> entityClass);
}
