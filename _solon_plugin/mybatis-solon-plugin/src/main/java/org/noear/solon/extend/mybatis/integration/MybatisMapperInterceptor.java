package org.noear.solon.extend.mybatis.integration;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.data.tran.TranUtils;

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
        try (SqlSession session = factory.openSession()) {
            Object mapper = session.getMapper(mapperClz);
            return method.invoke(mapper, args);
        }
    }
}
