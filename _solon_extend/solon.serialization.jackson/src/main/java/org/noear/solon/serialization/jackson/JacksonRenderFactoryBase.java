package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.noear.solon.serialization.JsonRenderFactory;
import org.noear.solon.serialization.JsonConverter;

import java.io.IOException;

/**
 * @author noear 2021/10/11 created
 */
public abstract class JacksonRenderFactoryBase implements JsonRenderFactory {

    public abstract ObjectMapper config();

    protected SimpleModule module;

    protected void registerModule() {
        if (module != null) {
            config().registerModule(module);
        }
    }


    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        if (module == null) {
            module = new SimpleModule();
        }

        module.addSerializer(clz, encoder);
    }

    @Override
    public <T> void addConvertor(Class<T> clz, JsonConverter<T> converter) {
        addEncoder(clz, new JsonSerializer<T>() {
            @Override
            public void serialize(T source, JsonGenerator out, SerializerProvider sp) throws IOException {
                Object val = converter.convert((T) source);

                if (val == null) {
                    out.writeNull();
                } else if (val instanceof String) {
                    out.writeString((String) val);
                } else if (val instanceof Number) {
                    if (val instanceof Integer || val instanceof Long) {
                        out.writeNumber(((Number) val).longValue());
                    } else {
                        out.writeNumber(((Number) val).doubleValue());
                    }
                } else {
                    throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
                }
            }
        });
    }
}
