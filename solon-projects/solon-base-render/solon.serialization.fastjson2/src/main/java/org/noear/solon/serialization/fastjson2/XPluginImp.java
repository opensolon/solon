package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSONWriter;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::renderFactory
        //绑定属性
        Fastjson2RenderFactory renderFactory = new Fastjson2RenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(Fastjson2RenderFactory.class, renderFactory);
        EventBus.push(renderFactory);

        //::renderTypedFactory
        Fastjson2RenderTypedFactory renderTypedFactory = new Fastjson2RenderTypedFactory();
        context.wrapAndPut(Fastjson2RenderTypedFactory.class, renderTypedFactory);


        context.lifecycle(-99, () -> {
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });

        //::actionExecutor
        //支持 json 内容类型执行
        Fastjson2ActionExecutor actionExecutor = new Fastjson2ActionExecutor();
        context.wrapAndPut(Fastjson2ActionExecutor.class, actionExecutor);
        EventBus.push(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }

    private void applyProps(Fastjson2RenderFactory factory, JsonProps jsonProps) {
        boolean writeNulls = false;

        if (JsonPropsUtil.apply(factory, jsonProps)) {
            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;

            if (jsonProps.nullStringAsEmpty) {
                factory.addFeatures(JSONWriter.Feature.WriteNullStringAsEmpty);
            }

            if (jsonProps.nullBoolAsFalse) {
                factory.addFeatures(JSONWriter.Feature.WriteNullBooleanAsFalse);
            }

            if (jsonProps.nullNumberAsZero) {
                factory.addFeatures(JSONWriter.Feature.WriteNullNumberAsZero);
            }

            if (jsonProps.nullArrayAsEmpty) {
                factory.addFeatures(JSONWriter.Feature.WriteNullListAsEmpty);
            }

            if(jsonProps.enumAsName){
                factory.addFeatures(JSONWriter.Feature.WriteEnumsUsingName);
            }

            if (writeNulls) {
                factory.addFeatures(JSONWriter.Feature.WriteNulls);
            }
        }
    }
}
