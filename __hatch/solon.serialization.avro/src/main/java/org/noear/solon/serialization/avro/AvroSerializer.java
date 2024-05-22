package org.noear.solon.serialization.avro;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.noear.solon.core.serialize.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author noear
 * @since 1.5
 */
public class AvroSerializer implements Serializer<String> {
    @Override
    public String name() {
        return "avro-bytes";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        DatumWriter datumWriter = new SpecificDatumWriter(obj.getClass());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);

        datumWriter.write(obj, encoder);

        return out.toString();
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
        return null;
    }
}
