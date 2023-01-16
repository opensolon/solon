package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.noear.solon.serialization.prop.JsonProps;

import java.io.IOException;
import java.util.Collection;

/**
 * @author noear
 * @since 1.12
 */
public class NullValueSerializer extends JsonSerializer<Object> implements ContextualSerializer {
    private Class<?> type;
    private JsonProps jsonProps;

    public NullValueSerializer(JsonProps jsonProps) {
        this.jsonProps = jsonProps;
    }

    public NullValueSerializer(JsonProps jsonProps, final JavaType type) {
        this.jsonProps = jsonProps;
        this.type = type == null ? Object.class : type.getRawClass();
    }

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (o == null) {
            if (jsonProps.nullStringAsEmpty && type == String.class) {
                jsonGenerator.writeString("");
                return;
            }

            if (jsonProps.nullBoolAsFalse && type == Boolean.class) {
                jsonGenerator.writeBoolean(false);
                return;
            }

            if (jsonProps.nullNumberAsZero && CharSequence.class.isAssignableFrom(type)) {
                if (type == Long.class) {
                    jsonGenerator.writeNumber(0L);
                } else if (type == Double.class) {
                    jsonGenerator.writeNumber(0.0D);
                } else if (type == Float.class) {
                    jsonGenerator.writeNumber(0.0F);
                } else {
                    jsonGenerator.writeNumber(0);
                }
                return;
            }

            if (jsonProps.nullArrayAsEmpty) {
                if (Collection.class.isAssignableFrom(type) || type.isArray()) {
                    jsonGenerator.writeStartArray();
                    jsonGenerator.writeEndArray();
                    return;
                }
            }

            if (jsonProps.nullAsWriteable) {
                jsonGenerator.writeNull();
                return;
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return new NullValueSerializer(jsonProps, beanProperty.getType());
    }
}
