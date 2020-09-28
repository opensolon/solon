package org.noear.fairy.decoder;

import io.edap.x.protobuf.ProtoBuf;
import org.noear.fairy.Enctype;
import org.noear.fairy.IDecoder;
import org.noear.fairy.Result;

public class ProtobufDecoder implements IDecoder {
    public static final ProtobufDecoder instance = new ProtobufDecoder();

    @Override
    public Enctype enctype() {
        return Enctype.application_protobuf;
    }

    @Override
    public <T> T decode(Result rst, Class<T> clz) {
        try {
            return ProtoBuf.toObject(rst.body(), clz);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
