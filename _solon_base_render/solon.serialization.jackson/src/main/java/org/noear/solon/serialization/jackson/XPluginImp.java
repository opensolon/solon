package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.prop.JsonProps;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
        JsonProps jsonProps = JsonProps.create(context);

        //绑定属性
        bindProps(JacksonRenderFactory.global, jsonProps);

        //事件扩展
        EventBus.push(JacksonRenderFactory.global);

        RenderManager.mapping("@json", JacksonRenderFactory.global.create());
        RenderManager.mapping("@type_json", JacksonRenderTypedFactory.global.create());

        //支持 json 内容类型执行
        JacksonActionExecutor executor = new JacksonActionExecutor();
        EventBus.push(executor);

        Bridge.actionExecutorAdd(executor);
    }

    private void bindProps(JacksonRenderFactory factory, JsonProps jsonProps) {
        if (Utils.isNotEmpty(jsonProps.dateAsFormat)) {
            factory.config().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            factory.config().setDateFormat(new SimpleDateFormat(jsonProps.dateAsFormat));

            if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                factory.config().setTimeZone(TimeZone.getTimeZone(jsonProps.dateAsTimeZone));
            }
        }

        if (jsonProps.longAsString) {
            factory.addConvertor(Long.class, e -> String.valueOf(e));
        }

        if (jsonProps.intAsString) {
            factory.addConvertor(Integer.class, e -> String.valueOf(e));
        }

        if (jsonProps.boolAsInt) {
            factory.addConvertor(Boolean.class, e -> (e ? 1 : 0));
        }
    }
}
