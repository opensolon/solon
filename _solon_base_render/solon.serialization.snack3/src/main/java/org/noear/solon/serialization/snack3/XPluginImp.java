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
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
        JsonProps jsonProps = JsonProps.create(context);

        //绑定属性
        applyProps(SnackRenderFactory.global, jsonProps);

        //事件扩展
        EventBus.push(SnackRenderFactory.global);

        RenderManager.mapping("@json", SnackRenderFactory.global.create());
        RenderManager.mapping("@type_json", SnackRenderTypedFactory.global.create());

        //支持 json 内容类型执行
        EventBus.push(SnackActionExecutor.global);

        Bridge.actionExecutorAdd(SnackActionExecutor.global);
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
        }
    }
}
