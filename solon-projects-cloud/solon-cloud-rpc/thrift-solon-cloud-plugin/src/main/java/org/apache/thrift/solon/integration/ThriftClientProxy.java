package org.apache.thrift.solon.integration;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.solon.annotation.ThriftClient;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFramedTransport;
import org.noear.solon.Utils;
import org.noear.solon.core.LoadBalance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client 代理类
 * <p>
 * 添加 {@link TTransport#open()} 和 {@link TTransport#close()} 的执行
 *
 * @author LIAO.Chunping
 */
public class ThriftClientProxy implements InvocationHandler {

    private final Map<String, Object> channelMap = new ConcurrentHashMap<>();
    private LoadBalance upstream;
    private ThriftClient clientAnno;
    private Class<?> clientType;

    public ThriftClientProxy(ThriftClient clientAnno, Class<?> clientType) {
        this.clientAnno = clientAnno;
        this.clientType = clientType;

        String name = Utils.annoAlias(clientAnno.value(), clientAnno.name());

        if (Utils.isEmpty(clientAnno.group())) {
            upstream = LoadBalance.get(name);
        } else {
            upstream = LoadBalance.get(clientAnno.group(), name);
        }

        if (upstream == null) {
            throw new IllegalStateException("No service upstream found: " + name);
        }
    }

    private Object getClient() {
        String server = upstream.getServer();

        Object real = channelMap.computeIfAbsent(server, k -> {
            URI uri = URI.create(k);
            try {
                return createClient(uri);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return real;
    }

    private Object createClient(URI uri) throws Exception {
        TTransport transport = new TSocket(uri.getHost(), uri.getPort());
        TFramedTransport tFramedTransport = new TFramedTransport(transport);
        tFramedTransport.open();

        TProtocol protocol = new TBinaryProtocol(tFramedTransport);
        TMultiplexedProtocol tMultiplexedProtocol = new TMultiplexedProtocol(protocol, clientAnno.serviceName());

        // 找到 Client 中，带 TMultiplexedProtocol.class 的构造器，将其初始化
        Constructor<?> declaredConstructor = clientType.getDeclaredConstructor(TProtocol.class);
        Object client = declaredConstructor.newInstance(tMultiplexedProtocol);
        return client;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object target = getClient();

        // getInputProtocol 与 getOutProtocol 方法获取的 TProtocol 都为同一个对象
//        Method getInputProtocolMethod = target.getClass().getMethod("getInputProtocol");
//        TProtocol inputProtocol = (TProtocol) getInputProtocolMethod.invoke(target);
//        TTransport transport = inputProtocol.getTransport();
//        if (!transport.isOpen()) {
//            transport.open();
//        }

        return method.invoke(target, args);

//        try {
//            Object result = method.invoke(target, args);
//            return result;
//        } finally {
//            if (transport.isOpen()) {
//                transport.close();
//            }
//        }
    }
}