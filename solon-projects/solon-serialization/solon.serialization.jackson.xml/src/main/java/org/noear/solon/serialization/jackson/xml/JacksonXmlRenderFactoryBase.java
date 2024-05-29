package org.noear.solon.serialization.jackson.xml;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.serialization.JsonRenderFactory;

import java.io.IOException;

/**
 * @author painter
 * @since 2.8
 */
public abstract class JacksonXmlRenderFactoryBase implements JsonRenderFactory {

    public abstract XmlMapper config();

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
    public <T> void addConvertor(Class<T> clz, Converter<T,Object> converter) {
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
