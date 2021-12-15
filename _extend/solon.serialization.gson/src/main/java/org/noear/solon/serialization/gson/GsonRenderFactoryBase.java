package org.noear.solon.serialization.gson;

import com.google.gson.*;
import org.noear.solon.serialization.JsonRenderFactory;
import org.noear.solon.serialization.JsonConverter;

/**
 * @author noear 2021/10/11 created
 */
public abstract class GsonRenderFactoryBase implements JsonRenderFactory {

    protected abstract GsonBuilder config();

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        config().registerTypeAdapter(clz, encoder);
    }


    @Override
    public <T> void addConvertor(Class<T> clz, JsonConverter<T> converter) {
        addEncoder(clz, (source, type, jsc) -> {
            Object val = converter.convert((T) source);

            if (val == null) {
                return JsonNull.INSTANCE;
            } else if (val instanceof String) {
                return new JsonPrimitive((String) val);
            } else if (val instanceof Number) {
                return new JsonPrimitive((Number) val);
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }
}
