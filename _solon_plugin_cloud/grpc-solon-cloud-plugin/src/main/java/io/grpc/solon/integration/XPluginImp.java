package io.grpc.solon.integration;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.solon.annotation.EnableGrpc;
import io.grpc.solon.annotation.GrpcService;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;
import io.grpc.solon.annotation.GrpcClient;

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

    public static String solon_boot_ver() {
        return "grpc 1.49.0/" + Solon.version();
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

        context.lifecycle(-99, () -> {
            startForServer(Solon.app());
        });
    }

    private void startForServer(SolonApp app) throws Throwable {
        if (serviceMap.size() == 0) {
            return;
        }

        GrpcServerProps props = new GrpcServerProps(25000);
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();

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

        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "grpc", SignalType.HTTP);

        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        LogUtil.global().info("Connector:main: grpc: Started ServerConnector@{grpc://localhost:" + _port + "}");
        LogUtil.global().info("Server:main: grpc: Started ("+solon_boot_ver()+") @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.shutdown();
            server = null;

            LogUtil.global().info("Server:main: grpc: Has Stopped (" + solon_boot_ver() + ")");
        }
    }
}
