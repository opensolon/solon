package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class JacksonRenderFactory extends JacksonRenderFactoryBase {

    ObjectMapper config = new ObjectMapper();

    public JacksonRenderFactory(){
        config.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        config.registerModule(new JavaTimeModule());
    }


    @Override
    public Render create() {
        registerModule();

        return new StringSerializerRender(false, new JacksonSerializer(config));
    }

    @Override
    public ObjectMapper config() {
        return config;
    }
}
