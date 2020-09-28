package org.noear.fairy.encoder;

import io.edap.x.protobuf.ProtoBuf;
import org.noear.fairy.Enctype;
import org.noear.fairy.IEncoder;

public class ProtobufEncoder implements IEncoder {
    public static final ProtobufEncoder instance = new ProtobufEncoder();

    @Override
    public Enctype enctype() {
        return Enctype.application_protobuf;
    }

    @Override
    public Object encode(Object obj) {
        try {
            return ProtoBuf.ser(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
