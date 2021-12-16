package org.noear.solon.serialization.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializer;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class GsonRenderFactory extends GsonRenderFactoryBase {
    public static final GsonRenderFactory global = new GsonRenderFactory();

    private final GsonBuilder config;
    private GsonRenderFactory() {
        config = new GsonBuilder()
                .registerTypeAdapter(java.util.Date.class, new GsonDateSerialize());
    }


    /**
     * 添加编码器
     * */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        config.registerTypeAdapter(clz, encoder);
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, serializer());
    }


    @Override
    public StringSerializer serializer() {
        return new GsonSerializer(config.create());
    }

    @Override
    protected GsonBuilder config() {
        return config;
    }
}
