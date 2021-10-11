package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.RenderFactory;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * @author noear 2021/10/11 created
 */
public class JacksonRenderTypedFactory extends JacksonCustomizer implements RenderFactory {
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
        return new StringSerializerRender(true, new JacksonSerializer(config));
    }

    @Override
    protected ObjectMapper config() {
        return config;
    }
}
