package feature.controller;

import feature.controller.service.*;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.UapiGateway;

@XController
@XMapping("/rpc/*")
public class RpcGateway extends UapiGateway {
    @Override
    protected boolean allowActionMapping() {
        return false;
    }

    @Override
    protected void register() {
        add(API_A_0_1.class);
        add(API_A_0_2.class);
        add(API_A_0_3.class);
        add(API_A_0_4.class);
    }
}
