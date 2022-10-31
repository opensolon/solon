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
    public static final Fastjson2RenderTypedFactory global = new Fastjson2RenderTypedFactory();

    private ObjectWriterProvider config;
    private JSONWriter.Feature[] features;

    private Fastjson2RenderTypedFactory(){
        features = new JSONWriter.Feature[]{
                JSONWriter.Feature.BrowserCompatible,
                JSONWriter.Feature.WriteClassName,
                JSONWriter.Feature.ReferenceDetection
        };
    }

    @Override
    public Render create() {
        return new StringSerializerRender(true, new Fastjson2Serializer(config,features));
    }

    @Override
    public ObjectWriterProvider config() {
        if(config == null){
            config = new ObjectWriterProvider();
        }

        return config;
    }

}
