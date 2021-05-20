package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author noear
 * @since 1.4
 */
public class ThrowableJsonSerializer extends JsonSerializer<Throwable> {

    @Override
    public void serialize(Throwable exception, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(byteArrayOutputStream);
        oo.writeObject(exception);
        oo.flush();
        oo.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        gen.writeRawValue("\"" + new String(bytes) + "\"");
    }
}
