package org.noear.solon.serialization.avro;

import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.ClassUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

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
    public Object deserialize(String data, Type toType) throws IOException {
        Class<?> clz = ClassUtil.getTypeClass(toType);

        DatumReader datumReader = new SpecificDatumReader(clz);
        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(in, null);

        return datumReader.read(null, decoder);
    }
}
