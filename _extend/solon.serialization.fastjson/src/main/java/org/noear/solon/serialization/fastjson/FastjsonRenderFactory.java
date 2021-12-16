package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializer;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class FastjsonRenderFactory extends FastjsonRenderFactoryBase {
    public static final FastjsonRenderFactory global = new FastjsonRenderFactory();

    private SerializeConfig config;
    private SerializerFeature[] features;

    private FastjsonRenderFactory(){
        features = new SerializerFeature[]{
                SerializerFeature.BrowserCompatible,
                SerializerFeature.DisableCircularReferenceDetect
        };
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, serializer());
    }

    @Override
    public StringSerializer serializer() {
        return new FastjsonSerializer(config, features);
    }

    @Override
    protected SerializeConfig config() {
        if (config == null) {
            config = new SerializeConfig();
        }

        return config;
    }
}
