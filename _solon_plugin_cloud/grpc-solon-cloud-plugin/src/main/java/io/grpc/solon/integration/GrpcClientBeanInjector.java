package io.grpc.solon.integration;

import io.grpc.Channel;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import io.grpc.solon.annotation.GrpcClient;

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
        String name = Utils.annoAlias(anno.value(), anno.name());

        if (grpcCli != null) {
            varH.setValue(grpcCli);
        } else {
            Channel grpcChannel = new GrpcChannelProxy(anno.group(), name);
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
                throw new RuntimeException(e);
            }
        }
    }
}
