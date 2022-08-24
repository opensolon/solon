package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.9
 */
public class Fastjson2RenderFactory extends Fastjson2RenderFactoryBase {
    public static final Fastjson2RenderFactory global = new Fastjson2RenderFactory();

    private ObjectWriterProvider config;
    private JSONWriter.Feature[] features;

    private Fastjson2RenderFactory(){
        features = new JSONWriter.Feature[]{
                JSONWriter.Feature.BrowserCompatible,
                JSONWriter.Feature.ReferenceDetection
        };
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, new Fastjson2Serializer(config,features));
    }

    /**
     * 重新设置特性
     * */
    public void setFeatures(JSONWriter.Feature... features) {
        this.features = features;
    }

    @Override
    public ObjectWriterProvider config() {
        if(config == null){
            config = new ObjectWriterProvider();
        }

        return config;
    }
}
