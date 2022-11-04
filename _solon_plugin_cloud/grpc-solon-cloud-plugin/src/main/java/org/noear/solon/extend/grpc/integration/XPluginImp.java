package org.noear.solon.extend.grpc.integration;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;
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
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    Server server;

    Map<Class<?>, Object> serviceMap;
    Map<Class<?>, Object> clientMap;

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableGrpc.class) == null) {
            return;
        }

        serviceMap = new HashMap<>();
        clientMap = new HashMap<>();

        context.beanBuilderAdd(GrpcService.class, new GrpcServiceBeanBuilder(serviceMap));
        context.beanInjectorAdd(GrpcClient.class, new GrpcClientBeanInjector(clientMap));

        context.beanOnloaded(ctx -> {
            try {
                startForServer(Solon.app());
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void startForServer(SolonApp app) throws Throwable {
        if (serviceMap.size() == 0) {
            return;
        }

        GrpcSignalProps props = new GrpcSignalProps(25000);
        String _host = props.getHost();
        int _port = props.getPort();
        String _name = props.getName();

        long time_start = System.currentTimeMillis();

        LogUtil.global().info("Server:main: io.grpc.Server(grpc)");

        ServerBuilder serverBuilder = ServerBuilder
                .forPort(_port);

        serviceMap.forEach((k, v) -> {
            if (v instanceof BindableService) {
                serverBuilder.addService((BindableService) v);
            }

            if (v instanceof ServerServiceDefinition) {
                serverBuilder.addService((ServerServiceDefinition) v);
            }
        });


        server = serverBuilder.build().start();

        _signal = new SignalSim(_name, _host, _port, "http", SignalType.HTTP);
        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        LogUtil.global().info("Connector:main: grpc: Started ServerConnector@{grpc://localhost:" + _port + "}");
        LogUtil.global().info("Server:main: grpc: Started @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}
