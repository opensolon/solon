package org.noear.solon.serialization.hession;

import org.noear.solon.XApp;
import org.noear.solon.core.XAction;
import org.noear.solon.core.XActionUtil;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    public static boolean output_meta = false;

    @Override
    public void start(XApp app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        HessionRender render = new HessionRender();

        //XRenderManager.register(render);
        XRenderManager.mapping("@hession",render);

        XActionUtil.converterSet.add(new HessianConverter());
    }
}
