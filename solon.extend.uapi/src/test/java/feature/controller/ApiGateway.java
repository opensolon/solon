package feature.controller;

import feature.controller.apis.API_0_0_0;
import feature.controller.apis.API_A_0_1;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.UApiGateway;
import org.noear.solon.extend.uapi.UApiRender;

@XController
@XMapping("/api/*")
public class ApiGateway extends UApiGateway {
    @Override
    public void register() {
        after(new UApiRender());

        add(API_0_0_0.class);
        add(API_A_0_1.class);
    }
}
