package org.apache.ibatis.solon.integration;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mybatis Mapper Interceptor
 *
 * @author noear
 * @since 1.6
 */
public class MybatisMapperInterceptor implements InvocationHandler {
    SqlSessionFactory factory;
    Class<?> mapperClz;

    public MybatisMapperInterceptor(SqlSessionFactory factory, Class<?> mapperClz) {
        this.factory = factory;
        this.mapperClz = mapperClz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (SqlSession session = factory.openSession(true)) {
            Object mapper = session.getMapper(mapperClz);
            return method.invoke(mapper, args);
        }
    }
}
