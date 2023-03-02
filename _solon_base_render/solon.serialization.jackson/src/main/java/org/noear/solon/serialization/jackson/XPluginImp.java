package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
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

        //::renderFactory
        //绑定属性
        JacksonRenderFactory renderFactory = new JacksonRenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(JacksonRenderFactory.class, renderFactory);
        EventBus.push(renderFactory);

        //::renderTypedFactory
        JacksonRenderTypedFactory renderTypedFactory = new JacksonRenderTypedFactory();
        context.wrapAndPut(JacksonRenderTypedFactory.class, renderTypedFactory);


        context.lifecycle(-99, () -> {
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });

        //支持 json 内容类型执行
        JacksonActionExecutor actionExecutor = new JacksonActionExecutor();
        context.wrapAndPut(JacksonActionExecutor.class, actionExecutor);
        EventBus.push(actionExecutor);

        Bridge.actionExecutorAdd(actionExecutor);
    }

    private void applyProps(JacksonRenderFactory factory, JsonProps jsonProps) {
        boolean writeNulls = false;

        if (JsonPropsUtil.apply(factory, jsonProps)) {

            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;

            if (writeNulls) {
//                factory.config()
//                        .getSerializerFactory()
//                        .withSerializerModifier(new NullBeanSerializerModifierImpl(jsonProps));
                factory.config()
                        .getSerializerProvider()
                        .setNullValueSerializer(new NullValueSerializer(jsonProps));
            }
        }

        if (writeNulls == false) {
            factory.config().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }

        //反序列化时候遇到不匹配的属性并不抛出异常
        factory.config().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化时候遇到空对象不抛出异常
        factory.config().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //反序列化的时候如果是无效子类型,不抛出异常
        factory.config().configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }
}
