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
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
        JsonProps jsonProps = JsonProps.create(context);

        //绑定属性
        applyProps(Fastjson2RenderFactory.global, jsonProps);

        //事件扩展
        EventBus.push(Fastjson2RenderFactory.global);

        RenderManager.mapping("@json", Fastjson2RenderFactory.global.create());
        RenderManager.mapping("@type_json", Fastjson2RenderTypedFactory.global.create());

        //支持 json 内容类型执行
        Fastjson2ActionExecutor executor = new Fastjson2ActionExecutor();
        EventBus.push(executor);

        Bridge.actionExecutorAdd(executor);
    }

    private void applyProps(Fastjson2RenderFactory factory, JsonProps jsonProps) {
        if (JsonPropsUtil.apply(factory, jsonProps)) {

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

            if (jsonProps.nullAsWriteable) {
                factory.addFeatures(JSONWriter.Feature.WriteNulls);
            }
        }
    }
}
