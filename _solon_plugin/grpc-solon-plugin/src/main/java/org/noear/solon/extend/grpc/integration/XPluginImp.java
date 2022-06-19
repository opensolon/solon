package org.noear.solon.extend.grpc.integration;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.grpc.annotation.EnableGrpc;
import org.noear.solon.extend.grpc.annotation.GrpcClient;
import org.noear.solon.extend.grpc.annotation.GrpcService;

import java.util.HashMap;
import java.util.Map;

// https://zhuanlan.zhihu.com/p/464658805

/**
 * @author noear
 * @since 1.9
 * */
public class XPluginImp implements Plugin {
    Server server;

    Map<Class<?>, Object> serviceMap;
    Map<Class<?>, Object> clientMap;

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableGrpc.class) == null) {
            return;
        }

        serviceMap = new HashMap<>();
        context.beanBuilderAdd(GrpcService.class, new GrpcServiceBeanBuilder(serviceMap));
        context.beanInjectorAdd(GrpcClient.class, new GrpcClientBeanInjector());

        context.beanOnloaded(ctx -> {
            startForServer(ctx);
        });
    }

    private void startForServer(AopContext context) {
        if (serviceMap.size() == 0) {
            return;
        }

        ServerBuilder serverBuilder = ServerBuilder
                .forPort(XPluginProps.serverPort);

        serviceMap.forEach((k, v) -> {
            if (v instanceof BindableService) {
                serverBuilder.addService((BindableService) v);
            }

            if (v instanceof ServerServiceDefinition) {
                serverBuilder.addService((ServerServiceDefinition) v);
            }
        });


        try {
            server = serverBuilder.build().start();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}
