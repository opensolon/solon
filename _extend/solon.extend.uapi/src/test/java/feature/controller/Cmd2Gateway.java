package feature.controller;

import feature.controller.cmds.*;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.DataThrowable;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.UapiCode;
import org.noear.solon.extend.uapi.UapiGateway;

import java.util.HashMap;
import java.util.Map;

@XController
@XMapping("/cmd2/*")
public class Cmd2Gateway extends UapiGateway {
    @Override
    protected void register() {

        add(CMD_A_0_1.class);
        add(CMD_A_0_2.class);
        add(CMD_A_0_3.class);
        add(CMD_A_0_4.class);
    }

    //替换自定义上下文
    @Override
    public XContext context(XContext ctx) {
        return new CmdContext(ctx, this);
    }

    @Override
    public void render(Object obj, XContext c) throws Throwable {
        if(obj instanceof UapiCode){
            UapiCode uc = (UapiCode)obj;
            Map<String,Object> map = new HashMap<>();

            map.put("code",uc.getCode());
            map.put("msg",uc.getDescription());

            super.render(map,c);
        }else {
            super.render(obj, c);
        }
    }
}
