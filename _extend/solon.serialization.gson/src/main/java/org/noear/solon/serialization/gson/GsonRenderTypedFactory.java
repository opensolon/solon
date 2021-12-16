package org.noear.solon.serialization.gson;

import com.google.gson.GsonBuilder;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializer;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 类型化渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class GsonRenderTypedFactory extends GsonRenderFactoryBase {
    public static final GsonRenderTypedFactory global = new GsonRenderTypedFactory();

    private final GsonBuilder config;
    private GsonRenderTypedFactory() {
        config = new GsonBuilder()
                .registerTypeAdapter(java.util.Date.class, new GsonDateSerialize());
    }


    @Override
    public Render create() {
        return new StringSerializerRender(true, serializer());
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
