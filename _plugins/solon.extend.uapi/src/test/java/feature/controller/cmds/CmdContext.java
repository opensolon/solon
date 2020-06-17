package feature.controller.cmds;

import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.UApiContext;

public class CmdContext extends UApiContext {
    public CmdContext(XContext ctx) {
        super(ctx);

        this.name = param("name");
    }

    public String name;
}
