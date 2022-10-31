package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
 */
public class FastjsonRenderFactory extends FastjsonRenderFactoryBase {
    public static final FastjsonRenderFactory global = new FastjsonRenderFactory();

    private SerializeConfig config;
    private Set<SerializerFeature> features;

    private FastjsonRenderFactory() {
        features = new HashSet<>();
        features.add(SerializerFeature.BrowserCompatible);
        features.add(SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, new FastjsonSerializer(config, features.toArray(new SerializerFeature[features.size()])));
    }

    @Override
    public SerializeConfig config() {
        if (config == null) {
            config = new SerializeConfig();
        }

        return config;
    }

    /**
     * 重新设置特性
     */
    public void setFeatures(SerializerFeature... features) {
        this.features.clear();
        this.features.addAll(Arrays.asList(features));
    }

    /**
     * 添加特性
     * */
    public void addFeatures(SerializerFeature... features) {
        this.features.addAll(Arrays.asList(features));
    }

    /**
     * 移除特性
     * */
    public void removeFeatures(SerializerFeature... features) {
        this.features.removeAll(Arrays.asList(features));
    }
}
