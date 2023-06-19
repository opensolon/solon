package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Feature;
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
        SnackRenderFactory renderFactory = new SnackRenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(SnackRenderFactory.class, renderFactory);
        EventBus.push(renderFactory);


        //::renderTypedFactory
        SnackRenderTypedFactory renderTypedFactory = new SnackRenderTypedFactory();
        context.wrapAndPut(SnackRenderTypedFactory.class, renderTypedFactory);


        context.lifecycle(-99, () ->{
            //晚点加载，给定制更多时机
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });

        //::actionExecutor
        //支持 json 内容类型执行
        SnackActionExecutor actionExecutor = new SnackActionExecutor();
        context.wrapAndPut(SnackActionExecutor.class, actionExecutor);
        EventBus.push(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }

    private void applyProps(SnackRenderFactory factory, JsonProps jsonProps) {
        if (JsonPropsUtil.apply(factory, jsonProps)) {
            if (jsonProps.nullStringAsEmpty) {
                factory.addFeatures(Feature.StringNullAsEmpty);
            }

            if (jsonProps.nullBoolAsFalse) {
                factory.addFeatures(Feature.BooleanNullAsFalse);
            }

            if (jsonProps.nullNumberAsZero) {
                factory.addFeatures(Feature.NumberNullAsZero);
            }

            if (jsonProps.nullArrayAsEmpty) {
                factory.addFeatures(Feature.ArrayNullAsEmpty);
            }

            if (jsonProps.nullAsWriteable) {
                factory.addFeatures(Feature.SerializeNulls);
            }

            if(jsonProps.enumAsName){
                factory.addFeatures(Feature.EnumUsingName);
            }
        }
    }
}
