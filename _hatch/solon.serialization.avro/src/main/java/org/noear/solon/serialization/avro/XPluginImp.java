package org.noear.solon.serialization.avro;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        //XRenderManager.register(render);
        RenderManager.mapping("@avro", new AvroStringRender());
    }
}
