package org.noear.solon.serialization.avro;

import org.noear.solon.Solon;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(Solon app) {
        output_meta = app.props().getInt("solon.output.meta", 0) > 0;

        //XRenderManager.register(render);
        Bridge.renderMapping("@avro", new AvroRender());
    }
}
