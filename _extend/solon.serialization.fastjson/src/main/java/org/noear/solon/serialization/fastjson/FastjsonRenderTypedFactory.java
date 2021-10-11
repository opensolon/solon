package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.RenderFactory;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * @author noear 2021/10/11 created
 */
public class FastjsonRenderTypedFactory extends FastjsonCustomizer implements RenderFactory {
    public static final FastjsonRenderTypedFactory global = new FastjsonRenderTypedFactory();

    private SerializeConfig config;
    private SerializerFeature[] features;

    private FastjsonRenderTypedFactory(){
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
    protected SerializeConfig config() {
        if (config == null) {
            config = new SerializeConfig();
        }

        return config;
    }
}
