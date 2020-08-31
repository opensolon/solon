package org.noear.solon.extend.mybatis;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SqlSessionInterceptor implements InvocationHandler {
    private SqlSessionFactory factory;

    public SqlSessionInterceptor(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlSession session = TranUtil.current();
        Boolean has_close = false;
        if (session == null) {
            has_close = true;
            session = factory.openSession(true); //Auto Commit
        }

        Object unwrapped = null;

        try {
            Object result = method.invoke(session, args);
            unwrapped = result;
        } catch (Throwable ex) {
            unwrapped = ExceptionUtil.unwrapThrowable(ex);
            throw (Throwable) unwrapped;
        } finally {
            if (session != null && has_close) {
                session.close();
            }
        }

        return unwrapped;
    }
}
