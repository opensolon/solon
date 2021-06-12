package org.noear.solon.extend.mybatis;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.data.tran.TranUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SqlSessionInterceptor implements InvocationHandler {
    private SqlSessionFactory factory;

    public SqlSessionInterceptor(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlSession session = factory.openSession(
                TranUtils.getConnection(factory.getConfiguration().getEnvironment().getDataSource()));

        Object unwrapped = null;

        try {
            Object result = method.invoke(session, args);
            unwrapped = result;
        } catch (Throwable ex) {
            unwrapped = ExceptionUtil.unwrapThrowable(ex);
            throw (Throwable) unwrapped;
        } finally {
            if (session != null && TranUtils.inTrans() == false) {
                session.close();
            }
        }

        return unwrapped;
    }
}
