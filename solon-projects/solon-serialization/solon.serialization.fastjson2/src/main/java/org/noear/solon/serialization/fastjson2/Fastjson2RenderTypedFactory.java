package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 类型化渲染器工厂
 *
 * @author 暮城留风
 * @since 1.10
 */
public class Fastjson2RenderTypedFactory extends Fastjson2RenderFactoryBase {
    private Fastjson2StringSerializer serializer = new Fastjson2StringSerializer();

    public Fastjson2RenderTypedFactory() {
        serializer.cfgWriteFeatures(false, true,
                JSONWriter.Feature.BrowserCompatible,
                JSONWriter.Feature.WriteClassName,
                JSONWriter.Feature.ReferenceDetection
        );
    }

    public Fastjson2StringSerializer getSerializer() {
        return serializer;
    }

    @Override
    public Render create() {
        return new StringSerializerRender(true, serializer);
    }

    @Override
    public ObjectWriterProvider config() {
        return serializer.getWriteContext().getProvider();
    }
}