package org.apache.thrift.solon.integration;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.solon.annotation.ThriftClient;
import org.noear.solon.Solon;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.proxy.asm.AsmProxy;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * 添加了 @ThriftClient 注解的字段进行注入对象
 *
 * @author LIAO.Chunping
 */
public class ThriftClientBeanInjector implements BeanInjector<ThriftClient> {

    private Map<Class<?>, Object> clientMap;

    public ThriftClientBeanInjector(Map<Class<?>, Object> clientMap) {
        this.clientMap = clientMap;
    }

    @Override
    public void doInject(VarHolder varH, ThriftClient anno) {
        Object thriftClient = clientMap.get(varH.getType());
        if (thriftClient != null) {
            varH.setValue(thriftClient);
            return;
        }
        try {
            // 找到 Client 中，带 TProtocol.class 的构造器，将其初始化
            Class<?> clientType = varH.getType();
            Constructor<?> declaredConstructor = clientType.getDeclaredConstructor(TProtocol.class);

            // 创建 Client 代理，在代理中将打开/关闭 Socket 连接
            Object clientProxy = AsmProxy.newProxyInstance(Solon.context(), new ThriftClientProxy(anno, clientType), clientType, declaredConstructor, (Object) null);
            varH.setValue(clientProxy);
            clientMap.put(clientType, clientProxy);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
