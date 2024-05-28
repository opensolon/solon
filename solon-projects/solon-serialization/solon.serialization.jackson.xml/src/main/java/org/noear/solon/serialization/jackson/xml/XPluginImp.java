package org.noear.solon.serialization.jackson.xml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.noear.solon.serialization.jackson.xml.impl.NullBeanSerializerModifierImpl;
import org.noear.solon.serialization.jackson.xml.impl.NullValueSerializerImpl;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

import static com.fasterxml.jackson.databind.MapperFeature.PROPAGATE_TRANSIENT_MARKER;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;

/**
 * Xml XPluginImp
 *
 * @author painter
 * @since 2.8
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::renderFactory
        //绑定属性
        JacksonXmlRenderFactory renderFactory = new JacksonXmlRenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(JacksonXmlRenderFactory.class, renderFactory);
        EventBus.publish(renderFactory);

        //::renderTypedFactory
        JacksonXmlRenderTypedFactory renderTypedFactory = new JacksonXmlRenderTypedFactory();
        context.wrapAndPut(JacksonXmlRenderTypedFactory.class, renderTypedFactory);


        context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> {
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });

        //支持 xml 内容类型执行
        JacksonXmlActionExecutor actionExecutor = new JacksonXmlActionExecutor();
        context.wrapAndPut(JacksonXmlActionExecutor.class, actionExecutor);
        EventBus.publish(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }

    private void applyProps(JacksonXmlRenderFactory factory, JsonProps jsonProps) {
        boolean writeNulls = false;

        if (JsonPropsUtil.apply(factory, jsonProps)) {

            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;

            if (writeNulls) {
                //Json处理下才会生效
//                factory.config()
//                        .getSerializerProvider()
//                        .setNullValueSerializer(new NullValueSerializerImpl(jsonProps));

                // 预留此项,Jackson过期版本支持
                //factory.config().enable(ToXmlFeature.WRITE_EMPTY_ELEMENT_FOR_NULL_ARRAY_ELEMENTS);

                // Xml配置处理,基本都是各类型空值写入
                SerializerFactory serializerFactory = BeanSerializerFactory.instance.withSerializerModifier(new NullBeanSerializerModifierImpl(jsonProps));
                factory.config().setSerializerFactory(serializerFactory);
            }

            if(jsonProps.enumAsName){
                factory.config().configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING,true);
            }
        }

        if (writeNulls == false) {
            factory.config().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }

        //启用 transient 关键字
        factory.config().configure(PROPAGATE_TRANSIENT_MARKER,true);
        //启用排序（即使用 LinkedHashMap）
        factory.config().configure(SORT_PROPERTIES_ALPHABETICALLY, true);
        //是否识别不带引号的key
        factory.config().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //是否识别单引号的key
        factory.config().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //浮点数默认类型（dubbod 转 BigDecimal）
        factory.config().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);


        //反序列化时候遇到不匹配的属性并不抛出异常
        factory.config().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化时候遇到空对象不抛出异常
        factory.config().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //反序列化的时候如果是无效子类型,不抛出异常
        factory.config().configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }
}
