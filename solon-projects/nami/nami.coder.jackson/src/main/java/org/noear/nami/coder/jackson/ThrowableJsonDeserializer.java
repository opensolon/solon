package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author noear
 * @since 1.4
 */
public class ThrowableJsonDeserializer extends JsonDeserializer<Throwable> {

    @Override
    public Exception deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String valueAsString = jsonParser.getValueAsString();
        byte[] bytes = valueAsString.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
        try {
            Exception ex = (Exception) ois.readObject();
            return ex;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            ois.close();
        }
    }
}
