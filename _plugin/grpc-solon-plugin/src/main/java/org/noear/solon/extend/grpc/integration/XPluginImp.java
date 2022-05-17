package org.noear.solon.extend.grpc.integration;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.grpc.EnableGrpc;


public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        if (Solon.global().source().getAnnotation(EnableGrpc.class) == null) {
            return;
        }
    }
}
