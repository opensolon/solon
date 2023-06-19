package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.SerializerFeature;
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
        FastjsonRenderFactory renderFactory = new FastjsonRenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(FastjsonRenderFactory.class, renderFactory);
        EventBus.push(renderFactory);


        //::renderTypedFactory
        FastjsonRenderTypedFactory renderTypedFactory = new FastjsonRenderTypedFactory();
        context.wrapAndPut(FastjsonRenderTypedFactory.class, renderTypedFactory);

        context.lifecycle(-99, () ->{
            //晚点加载，给定制更多时机
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });

        //::actionExecutor
        //支持 json 内容类型执行
        FastjsonActionExecutor actionExecutor = new FastjsonActionExecutor();
        context.wrapAndPut(FastjsonActionExecutor.class, actionExecutor);
        EventBus.push(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }

    private void applyProps(FastjsonRenderFactory factory, JsonProps jsonProps) {
        if (JsonPropsUtil.apply(factory, jsonProps)) {

            if (jsonProps.nullStringAsEmpty) {
                factory.addFeatures(SerializerFeature.WriteNullStringAsEmpty);
            }

            if (jsonProps.nullBoolAsFalse) {
                factory.addFeatures(SerializerFeature.WriteNullBooleanAsFalse);
            }

            if (jsonProps.nullNumberAsZero) {
                factory.addFeatures(SerializerFeature.WriteNullNumberAsZero);
            }

            if (jsonProps.nullArrayAsEmpty) {
                factory.addFeatures(SerializerFeature.WriteNullListAsEmpty);
            }

            if(jsonProps.nullAsWriteable){
                factory.addFeatures(SerializerFeature.WriteMapNullValue);
            }

            if(jsonProps.enumAsName){
                factory.addFeatures(SerializerFeature.WriteEnumUsingName);
            }
        }
    }
}
