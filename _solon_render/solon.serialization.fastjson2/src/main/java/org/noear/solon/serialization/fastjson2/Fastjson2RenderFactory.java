package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Json 渲染器工厂
 *
 * @author 暮城留风
 * @since 1.10
 */
public class Fastjson2RenderFactory extends Fastjson2RenderFactoryBase {
    public static final Fastjson2RenderFactory global = new Fastjson2RenderFactory();

    private ObjectWriterProvider config;
    private Set<JSONWriter.Feature> features;

    private Fastjson2RenderFactory() {
        features = new HashSet<>();
        features.add(JSONWriter.Feature.BrowserCompatible);
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, new Fastjson2Serializer(config, features.toArray(new JSONWriter.Feature[features.size()])));
    }


    @Override
    public ObjectWriterProvider config() {
        if (config == null) {
            config = new ObjectWriterProvider();
        }

        return config;
    }

    /**
     * 重新设置特性
     */
    public void setFeatures(JSONWriter.Feature... features) {
        this.features.clear();
        this.features.addAll(Arrays.asList(features));
    }

    /**
     * 添加特性
     * */
    public void addFeatures(JSONWriter.Feature... features) {
        this.features.addAll(Arrays.asList(features));
    }

    /**
     * 移除特性
     * */
    public void removeFeatures(JSONWriter.Feature... features) {
        this.features.removeAll(Arrays.asList(features));
    }
}
