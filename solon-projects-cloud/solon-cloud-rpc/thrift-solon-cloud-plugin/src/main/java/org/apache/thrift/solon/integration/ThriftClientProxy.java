package org.apache.thrift.solon.integration;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Client 代理类
 * <p>
 * 添加 {@link TTransport#open()} 和 {@link TTransport#close()} 的执行
 *
 * @author LIAO.Chunping
 */
public class ThriftClientProxy implements InvocationHandler {

    private final Object target;

    public ThriftClientProxy(Object client) {
        this.target = client;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // getInputProtocol 与 getOutProtocol 方法获取的 TProtocol 都为同一个对象
        Method getInputProtocolMethod = target.getClass().getMethod("getInputProtocol");
        TProtocol inputProtocol = (TProtocol) getInputProtocolMethod.invoke(target);
        TTransport transport = inputProtocol.getTransport();
        transport.open();
        Object result = method.invoke(target, args);
        transport.close();
        return result;
    }
}
