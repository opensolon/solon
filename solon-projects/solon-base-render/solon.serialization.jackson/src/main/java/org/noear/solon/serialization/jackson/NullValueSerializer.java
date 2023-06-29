package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import org.noear.solon.serialization.prop.JsonProps;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author noear
 * @since 1.12
 */
public class NullValueSerializer extends JsonSerializer<Object> {
    private JsonProps jsonProps;
    public Class<?> type0;

    public NullValueSerializer(JsonProps jsonProps) {
        this.jsonProps = jsonProps;
    }

    public NullValueSerializer(JsonProps jsonProps, final JavaType type) {
        this.jsonProps = jsonProps;
        this.type0 = type == null ? Object.class : type.getRawClass();
    }

    @Override
    public void serialize(Object o, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        Class<?> type = type0;

        if (type == null) {
            try {
                if(gen.getCurrentValue() != null) {
                    String fieldName = gen.getOutputContext().getCurrentName();
                    Field field = gen.getCurrentValue().getClass().getDeclaredField(fieldName);
                    type = field.getType();
                }
            } catch (NoSuchFieldException e) {
            }
        }

        if (type != null) {
            if (jsonProps.nullStringAsEmpty && type == String.class) {
                gen.writeString("");
                return;
            }

            if (jsonProps.nullBoolAsFalse && type == Boolean.class) {
                if (jsonProps.boolAsInt) {
                    gen.writeNumber(0);
                } else {
                    gen.writeBoolean(false);
                }
                return;
            }

            if (jsonProps.nullNumberAsZero && Number.class.isAssignableFrom(type)) {
                if(jsonProps.longAsString && type == Long.class){
                    gen.writeString("0");
                }else{
                    if (type == Long.class) {
                        gen.writeNumber(0L);
                    } else if (type == Double.class) {
                        gen.writeNumber(0D);
                    } else if (type == Float.class) {
                        gen.writeNumber(0F);
                    } else {
                        gen.writeNumber(0);
                    }
                }

                return;
            }

            if (jsonProps.nullArrayAsEmpty) {
                if (Collection.class.isAssignableFrom(type) || type.isArray()) {
                    gen.writeStartArray();
                    gen.writeEndArray();
                    return;
                }
            }
        }

        gen.writeNull();
    }
}
