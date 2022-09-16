package org.noear.solon.extend.grpc.integration;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.grpc.annotation.GrpcService;

import java.util.Map;

/**
 * @author noear
 * @since 1.9
 */
public class GrpcServiceBeanBuilder implements BeanBuilder<GrpcService> {
    Map<Class<?>, Object> serviceMap;

    public GrpcServiceBeanBuilder(Map<Class<?>, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, GrpcService anno) throws Throwable {
        serviceMap.put(clz, bw.raw());
    }
}
