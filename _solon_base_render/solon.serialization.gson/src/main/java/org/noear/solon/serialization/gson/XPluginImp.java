package org.noear.solon.serialization.gson;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.gson.impl.*;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

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
        boolean writeNulls = false;

        if (JsonPropsUtil.apply(factory, jsonProps)) {
            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;

            if(jsonProps.nullNumberAsZero){
                factory.config().registerTypeAdapter(Number.class, new NullNumberSerialize());
            }

            if(jsonProps.nullArrayAsEmpty){
                factory.config().registerTypeAdapter(Collection.class, new NullCollectionSerialize());
                factory.config().registerTypeAdapter(Arrays.class, new NullArraySerialize());
            }

            if(jsonProps.nullBoolAsFalse){
                factory.config().registerTypeAdapter(Boolean.class, new NullBooleanSerialize());
            }

            if(jsonProps.nullStringAsEmpty){
                factory.config().registerTypeAdapter(String.class, new NullStringSerialize());
            }

            if (writeNulls) {
                factory.config().serializeNulls();
            }

        } else {
            //默认为时间截
            factory.config().registerTypeAdapter(Date.class, new GsonDateSerialize());
        }
    }
}
