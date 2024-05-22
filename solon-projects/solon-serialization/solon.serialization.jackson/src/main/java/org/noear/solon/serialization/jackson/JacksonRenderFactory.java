package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class JacksonRenderFactory extends JacksonRenderFactoryBase {

    ObjectMapper config = new ObjectMapper();

    private Set<SerializationFeature> features;

    public JacksonRenderFactory() {
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

        JacksonStringSerializer serializer = new JacksonStringSerializer();
        serializer.setConfig(config);

        return new StringSerializerRender(false, serializer);
    }

    @Override
    public ObjectMapper config() {
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
