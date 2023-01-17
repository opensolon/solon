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

        //::renderFactory
        //绑定属性
        GsonRenderFactory renderFactory = new GsonRenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(GsonRenderFactory.class, renderFactory);
        EventBus.push(renderFactory);

        //::renderTypedFactory
        GsonRenderTypedFactory renderTypedFactory = new GsonRenderTypedFactory();
        context.wrapAndPut(GsonRenderTypedFactory.class, renderTypedFactory);

        context.beanOnloaded(x -> {
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });
    }

    private void applyProps(GsonRenderFactory factory, JsonProps jsonProps) {
        if (JsonPropsUtil.apply(factory, jsonProps)) {

        } else {
            //默认为时间截
            factory.config().registerTypeAdapter(java.util.Date.class, new GsonDateSerialize());
        }
    }
}
