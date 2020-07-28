package org.noear.solon.serialization.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.Date;

public class DateSerializer<T extends Date> extends JsonSerializer<T> {
    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider ctx) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        gen.writeNumber(value.getTime());
    }
}
