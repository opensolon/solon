package io.grpc.solon.integration;

import org.noear.solon.boot.prop.impl.BaseServerProps;

/**
 * @author noear
 * @since 1.8
 */
public class GrpcServerProps extends BaseServerProps {
    public GrpcServerProps(int portBase) {
        super("grpc", portBase);
    }
}
