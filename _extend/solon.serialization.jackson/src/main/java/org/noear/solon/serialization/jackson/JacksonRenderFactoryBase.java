package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.noear.solon.serialization.JsonRenderFactory;
import org.noear.solon.serialization.LongConverter;
import org.noear.solon.serialization.StringConverter;

import java.io.IOException;

/**
 * @author noear 2021/10/11 created
 */
public abstract class JacksonRenderFactoryBase implements JsonRenderFactory {

    protected abstract ObjectMapper config();

    protected  SimpleModule module;


    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        if (module == null) {
            module = new SimpleModule();
            config().registerModule(module);
        }

        module.addSerializer(clz, encoder);
    }

    @Override
    public <T> void addConvertor(Class<T> clz, LongConverter<T> converter) {
        addEncoder(clz, new JsonSerializer<T>() {
            @Override
            public void serialize(T source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeNumber(converter.convert(source).longValue());
            }
        });
    }

    @Override
    public <T> void addConvertor(Class<T> clz, StringConverter<T> converter) {
        addEncoder(clz, new JsonSerializer<T>() {
            @Override
            public void serialize(T source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeString(converter.convert(source));
            }
        });
    }
}
