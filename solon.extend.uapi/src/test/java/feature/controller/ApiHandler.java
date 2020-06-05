package feature.controller;

import feature.controller.apis.API_0_0_0;
import feature.controller.apis.API_A_0_1;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.UApiNav;
import org.noear.solon.extend.uapi.UApiRender;

@XController
@XMapping("/api/*")
public class ApiHandler extends UApiNav {
    @Override
    public void register() {
        after(new UApiRender());

        add(new API_0_0_0());
        add(new API_A_0_1());
    }
}
