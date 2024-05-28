package org.noear.solon.serialization.jackson.xml;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 类型化渲染器工厂
 *
 * @author painter
 * @since 1.5
 * @since 2.8
 */
public class JacksonXmlRenderTypedFactory extends JacksonXmlRenderFactoryBase {
    XmlMapper config = new XmlMapper();

    public JacksonXmlRenderTypedFactory(){
        config.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        config.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        config.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        config.activateDefaultTypingAsProperty(
                config.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        config.registerModule(new JavaTimeModule());
    }

    @Override
    public Render create() {
        registerModule();

        JacksonXmlStringSerializer serializer = new JacksonXmlStringSerializer();
        serializer.setConfig(config);

        return new StringSerializerRender(true, serializer);
    }

    @Override
    public XmlMapper config() {
        return config;
    }
}
