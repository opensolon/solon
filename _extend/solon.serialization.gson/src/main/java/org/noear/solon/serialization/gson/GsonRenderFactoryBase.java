package org.noear.solon.serialization.gson;

import com.google.gson.*;
import org.noear.solon.serialization.JsonRenderFactory;
import org.noear.solon.serialization.JsonLongConverter;
import org.noear.solon.serialization.JsonStringConverter;

/**
 * @author noear 2021/10/11 created
 */
public abstract class GsonRenderFactoryBase implements JsonRenderFactory {

    protected abstract GsonBuilder config();

    /**
     * 添加编码器
     * */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        config().registerTypeAdapter(clz, encoder);
    }

    @Override
    public <T> void addConvertor(Class<T> clz, JsonLongConverter<T> converter) {
        addEncoder(clz, ((source, type, jsonSerializationContext) -> {
            return new JsonPrimitive(converter.convert(source));
        }));
    }

    @Override
    public <T> void addConvertor(Class<T> clz, JsonStringConverter<T> converter) {
        addEncoder(clz, ((source, type, jsonSerializationContext) -> {
            return new JsonPrimitive(converter.convert(source));
        }));
    }
}
