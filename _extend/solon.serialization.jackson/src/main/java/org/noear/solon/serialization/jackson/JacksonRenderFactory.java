package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializer;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * @author noear 2021/10/11 created
 */
public class JacksonRenderFactory extends JacksonRenderFactoryBase {
    public static final JacksonRenderFactory global = new JacksonRenderFactory();

    ObjectMapper config = new ObjectMapper();

    private JacksonRenderFactory(){
        config.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    @Override
    public Render create() {
        registerModule();

        return new StringSerializerRender(false, serializer());
    }

    protected StringSerializer serializer() {
        return new JacksonSerializer(config);
    }

    @Override
    protected ObjectMapper config() {
        return config;
    }
}
