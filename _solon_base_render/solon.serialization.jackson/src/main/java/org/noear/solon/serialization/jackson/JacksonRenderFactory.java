package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.core.JsonParser;
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
        // 允许使用未带引号的字段名
        config.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许使用单引号
        config.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
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
