package feature.controller;

import feature.controller.service.*;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.Result;
import org.noear.solon.extend.uapi.UapiCode;
import org.noear.solon.extend.uapi.UapiGateway;

@XController
@XMapping("/cmd2/*")
public class CmdGateway extends UapiGateway {
    @Override
    protected void register() {

        add(API_A_0_1.class);
        add(API_A_0_2.class);
        add(API_A_0_3.class);
        add(API_A_0_4.class);
    }

    @Override
    public void render(Object obj, XContext c) throws Throwable {
        if (obj instanceof UapiCode) {
            c.render(Result.failure((UapiCode) obj));
        } else if (obj instanceof Throwable) {
            c.render(Result.failure(new UapiCode((Throwable) obj)));
        } else{
            c.render(Result.succeed(obj));
        }
    }
}
