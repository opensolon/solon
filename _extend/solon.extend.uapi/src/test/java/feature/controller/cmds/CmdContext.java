package feature.controller.cmds;

import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.UapiContext;
import org.noear.solon.extend.uapi.UapiGateway;

public class CmdContext extends UapiContext {
    public CmdContext(XContext ctx, UapiGateway gateway) {
        super(ctx ,gateway);

        this.name = param("name");
    }

    public String name;
}
