package feature.controller.cmds;

import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.UApiContext;
import org.noear.solon.extend.uapi.UApiGateway;

public class CmdContext extends UApiContext {
    public CmdContext(XContext ctx, UApiGateway gateway) {
        super(ctx ,gateway);

        this.name = param("name");
    }

    public String name;
}
