package org.noear.solon.serialization.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public class CollectionSerializer<T extends Collection> extends JsonSerializer<T> {
    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider ctx) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

    }
}
