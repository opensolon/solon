package org.noear.solon.serialization.jackson.xml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Json 渲染器工厂
 *
 * @author painter
 * @since 2.8
 */
public class JacksonXmlRenderFactory extends JacksonXmlRenderFactoryBase {

    XmlMapper config = new XmlMapper();

    private Set<SerializationFeature> features;

    public JacksonXmlRenderFactory() {
        features = new HashSet<>();
        features.add(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        config.registerModule(new JavaTimeModule());
    }


    @Override
    public Render create() {
        registerModule();

        for (SerializationFeature f1 : features) {
            config.enable(f1);
        }

        JacksonXmlStringSerializer serializer = new JacksonXmlStringSerializer();
        serializer.setConfig(config);

        return new StringSerializerRender(false, JacksonXmlActionExecutor.label, serializer);
    }

    @Override
    public XmlMapper config() {
        return config;
    }


    /**
     * 重新设置特性
     */
    public void setFeatures(SerializationFeature... features) {
        this.features.clear();
        this.features.addAll(Arrays.asList(features));
    }

    /**
     * 添加特性
     */
    public void addFeatures(SerializationFeature... features) {
        this.features.addAll(Arrays.asList(features));
    }

    /**
     * 移除特性
     */
    public void removeFeatures(SerializationFeature... features) {
        this.features.removeAll(Arrays.asList(features));
    }
}
