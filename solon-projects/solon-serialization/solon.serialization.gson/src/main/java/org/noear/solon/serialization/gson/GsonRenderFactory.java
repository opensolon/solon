package org.noear.solon.serialization.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class GsonRenderFactory extends GsonRenderFactoryBase {
    private final GsonStringSerializer serializer = new GsonStringSerializer();

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        serializer.getConfig().registerTypeAdapter(clz, encoder);
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, GsonActionExecutor.label, serializer);
    }

    @Override
    public GsonBuilder config() {
        return serializer.getConfig();
    }
}
