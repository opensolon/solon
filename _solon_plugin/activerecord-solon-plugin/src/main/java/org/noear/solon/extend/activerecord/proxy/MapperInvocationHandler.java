package org.noear.solon.extend.activerecord.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jfinal.plugin.activerecord.Model;

/**
 * 每个 Mapper 接口对应一个 InvocationHandler
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10.7
 */
public class MapperInvocationHandler implements InvocationHandler {

    private String db;

    public MapperInvocationHandler(Class<? extends Model<?>> clz, String db) {
        MapperContextParser.parse(clz);
        this.db = db;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MapperMethodContext context = MapperMethodContextManager.getMethodContext(method);
        if (null == context) {
            throw new RuntimeException("method not resolved yet.");
        }

        return MapperMethodInvoker.invoke(context, this.db, args);
    }

}
