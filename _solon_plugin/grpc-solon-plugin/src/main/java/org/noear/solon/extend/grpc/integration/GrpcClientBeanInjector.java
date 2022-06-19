package org.noear.solon.extend.grpc.integration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.extend.grpc.annotation.GrpcClient;

/**
 * @author noear
 * @since 1.9
 */
public class GrpcClientBeanInjector implements BeanInjector<GrpcClient> {
    @Override
    public void doInject(VarHolder varH, GrpcClient anno) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(anno.name()).usePlaintext().build();


    }
}
