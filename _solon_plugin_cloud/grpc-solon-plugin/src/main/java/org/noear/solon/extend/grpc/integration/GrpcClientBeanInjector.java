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
import java.util.Map;

/**
 * @author noear
 * @since 1.9
 */
public class GrpcClientBeanInjector implements BeanInjector<GrpcClient> {
    Map<Class<?>, Object> clientMap;

    public GrpcClientBeanInjector(Map<Class<?>, Object> clientMap) {
        this.clientMap = clientMap;
    }

    @Override
    public void doInject(VarHolder varH, GrpcClient anno) {
        Method method;
        Object grpcCli = clientMap.get(varH.getType());
        String target = Utils.annoAlias(anno.value(), anno.name());

        if (grpcCli != null) {
            varH.setValue(grpcCli);
        } else {
            ManagedChannel grpcChannel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            Class<?> grpcClz = Utils.loadClass(varH.getType().getName().split("\\$")[0]);

            try {
                if (AbstractBlockingStub.class.isAssignableFrom(varH.getType())) {
                    method = grpcClz.getDeclaredMethod("newBlockingStub", Channel.class);
                    grpcCli = method.invoke(null, new Object[]{grpcChannel});
                }

                if (AbstractFutureStub.class.isAssignableFrom(varH.getType())) {
                    method = grpcClz.getDeclaredMethod("newFutureStub", Channel.class);
                    grpcCli = method.invoke(null, new Object[]{grpcChannel});
                }

                if (grpcCli != null) {
                    clientMap.put(varH.getType(), grpcCli);
                    varH.setValue(grpcCli);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
