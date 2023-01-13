package org.noear.solon.serialization.gson;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
        JsonProps jsonProps = JsonProps.create(context);

        //绑定属性
        JsonPropsUtil.apply(GsonRenderFactory.global, jsonProps);

        //事件扩展
        EventBus.push(GsonRenderFactory.global);

        RenderManager.mapping("@json", GsonRenderFactory.global.create());
        RenderManager.mapping("@type_json", GsonRenderTypedFactory.global.create());
    }
}
