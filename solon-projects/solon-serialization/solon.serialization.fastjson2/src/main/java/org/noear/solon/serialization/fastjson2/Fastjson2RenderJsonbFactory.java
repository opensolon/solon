package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.BytesSerializerRender;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 类型化渲染器工厂
 *
 * @author 暮城留风
 * @since 1.10
 */
public class Fastjson2RenderJsonbFactory extends Fastjson2RenderFactoryBase {
    private Fastjson2BytesSerializer serializer = new Fastjson2BytesSerializer();

    public Fastjson2RenderJsonbFactory() {
        serializer.cfgSerializeFeatures(false, true,
                JSONWriter.Feature.BrowserCompatible,
                JSONWriter.Feature.WriteClassName,
                JSONWriter.Feature.ReferenceDetection
        );
    }

    public Fastjson2BytesSerializer getSerializer() {
        return serializer;
    }

    @Override
    public Render create() {
        return new BytesSerializerRender( serializer, "application/jsonb");
    }

    @Override
    public ObjectWriterProvider config() {
        return serializer.getSerializeConfig().getProvider();
    }
}