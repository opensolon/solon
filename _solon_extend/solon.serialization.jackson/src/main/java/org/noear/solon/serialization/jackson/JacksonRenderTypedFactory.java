package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 类型化渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class JacksonRenderTypedFactory extends JacksonRenderFactoryBase {
    public static final JacksonRenderTypedFactory global = new JacksonRenderTypedFactory();

    ObjectMapper config = new ObjectMapper();

    private JacksonRenderTypedFactory(){
        config.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        config.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        config.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        config.activateDefaultTypingAsProperty(
                config.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "@type");
    }

    @Override
    public Render create() {
        registerModule();

        return new StringSerializerRender(true, new JacksonSerializer(config));
    }

    @Override
    protected ObjectMapper config() {
        return config;
    }
}
