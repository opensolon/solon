package org.noear.solon.extend.grpc.integration;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.extend.grpc.annotation.GrpcClient;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.9
 */
public class GrpcClientBeanInjector implements BeanInjector<GrpcClient> {
    @Override
    public void doInject(VarHolder varH, GrpcClient anno) {
        ManagedChannel grpcChannel = ManagedChannelBuilder.forTarget(anno.name()).usePlaintext().build();
        Class<?> grpcClz = Utils.loadClass(varH.getType().getName().split("\\$")[0]);

        try {
            if (AbstractBlockingStub.class.isAssignableFrom(varH.getType())) {
                Method method = grpcClz.getDeclaredMethod("newBlockingStub", Channel.class);
                Object grpcCli = method.invoke(null, new Object[]{grpcChannel});
                varH.setValue(grpcCli);
            }

            if (AbstractFutureStub.class.isAssignableFrom(varH.getType())) {
                Method method = grpcClz.getDeclaredMethod("newFutureStub", Channel.class);
                Object grpcCli = method.invoke(null, new Object[]{grpcChannel});
                varH.setValue(grpcCli);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
