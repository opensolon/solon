package org.apache.thrift.solon.integration;

import org.apache.thrift.solon.annotation.ThriftService;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

import java.util.Map;

/**
 * 针对添加了 @ThriftService 的类进行构建
 *
 * @author LIAO.Chunping
 */
public class ThriftServiceBeanBuilder implements BeanBuilder<ThriftService> {

    private Map<Class<?>, Object> serviceMap;

    public ThriftServiceBeanBuilder(Map<Class<?>, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, ThriftService anno) throws Throwable {
        serviceMap.put(clz, bw.raw());
    }
}
