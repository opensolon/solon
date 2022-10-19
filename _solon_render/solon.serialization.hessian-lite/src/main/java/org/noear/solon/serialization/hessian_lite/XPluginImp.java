package org.noear.solon.serialization.hessian_lite;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;

        HessianLiteRender render = new HessianLiteRender();

        //XRenderManager.register(render);
        RenderManager.mapping("@hessian",render);
        Bridge.actionExecutorAdd(new HessianLiteActionExecutor());
    }
}
