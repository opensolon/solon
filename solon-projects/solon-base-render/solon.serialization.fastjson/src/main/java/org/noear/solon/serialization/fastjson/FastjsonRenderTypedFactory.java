package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 类型化渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class FastjsonRenderTypedFactory extends FastjsonRenderFactoryBase {
    private SerializeConfig config;
    private SerializerFeature[] features;

    public FastjsonRenderTypedFactory(){
        features = new SerializerFeature[]{
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect
        };
    }

    @Override
    public Render create() {
        return new StringSerializerRender(true, new FastjsonSerializer(config, features));
    }

    @Override
    public SerializeConfig config() {
        if (config == null) {
            config = new SerializeConfig();
        }

        return config;
    }
}
