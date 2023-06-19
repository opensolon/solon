package org.noear.solon.serialization.gson;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.gson.impl.*;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
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

        context.lifecycle(-99, () -> {
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });

        //::actionExecutor
        //支持 json 内容类型执行
        GsonActionExecutor actionExecutor = new GsonActionExecutor();
        context.wrapAndPut(GsonActionExecutor.class, actionExecutor);
        EventBus.push(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }

    private void applyProps(GsonRenderFactory factory, JsonProps jsonProps) {
        boolean writeNulls = false;

        if (JsonPropsUtil.apply(factory, jsonProps)) {
            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;


            if (writeNulls) {
                factory.config().serializeNulls();
            }

            if(jsonProps.nullNumberAsZero){
                factory.config().registerTypeAdapter(Short.class, new NullNumberSerialize<Short>());
                factory.config().registerTypeAdapter(Integer.class, new NullNumberSerialize<Integer>());

                factory.config().registerTypeAdapter(Long.class, new NullLongAdapter(jsonProps));

                factory.config().registerTypeAdapter(Float.class, new NullNumberSerialize<Float>());
                factory.config().registerTypeAdapter(Double.class, new NullNumberSerialize<Double>());
            }

            if(jsonProps.nullArrayAsEmpty){
                factory.config().registerTypeAdapter(Collection.class, new NullCollectionSerialize());
                factory.config().registerTypeAdapter(Arrays.class, new NullArraySerialize());
            }

            if(jsonProps.nullBoolAsFalse){
                factory.config().registerTypeAdapter(Boolean.class, new NullBooleanAdapter(jsonProps));
            }

            if(jsonProps.nullStringAsEmpty){
                factory.config().registerTypeAdapter(String.class, new NullStringSerialize());
            }

            if(jsonProps.enumAsName){
                factory.config().registerTypeAdapter(Enum.class, new EnumAdapter());
            }

        } else {
            //默认为时间截
            factory.config().registerTypeAdapter(Date.class, new GsonDateSerialize());
        }
    }
}
