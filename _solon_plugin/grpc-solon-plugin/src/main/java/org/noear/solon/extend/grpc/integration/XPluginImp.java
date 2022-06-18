package org.noear.solon.extend.grpc.integration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.grpc.annotation.EnableGrpc;

// https://zhuanlan.zhihu.com/p/464658805

public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableGrpc.class) == null) {
            return;
        }

        context.beanOnloaded(ctx -> {

        });
    }

    private void startForServer(AopContext context) {
        int port = 9091;
//        Server server = ServerBuilder
//                .forPort(port)
//                .addService(new UserServiceImpl())
//                .build()
//                .start();

        System.out.println("server started, port : " + port);
    }

    private void startForClient(AopContext context) {

    }
}
