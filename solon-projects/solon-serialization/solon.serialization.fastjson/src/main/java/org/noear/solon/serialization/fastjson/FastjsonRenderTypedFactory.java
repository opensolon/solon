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
 * @since 2.8
 */
public class FastjsonRenderTypedFactory extends FastjsonRenderFactoryBase {
    private final FastjsonStringSerializer serializer = new FastjsonStringSerializer();

    public FastjsonRenderTypedFactory() {
        serializer.cfgSerializerFeatures(false, true,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public Render create() {
        return new StringSerializerRender(true, serializer);
    }

    @Override
    public SerializeConfig config() {
        return serializer.getSerializeConfig();
    }
}
