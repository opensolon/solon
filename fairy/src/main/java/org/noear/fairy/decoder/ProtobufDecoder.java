package org.noear.fairy.decoder;

import io.edap.x.protobuf.ProtoBuf;
import org.noear.fairy.Enctype;
import org.noear.fairy.FairyConfig;
import org.noear.fairy.IDecoder;
import org.noear.fairy.Result;
import org.noear.fairy.channel.Constants;

import java.util.Map;

public class ProtobufDecoder implements IDecoder {
    public static final ProtobufDecoder instance = new ProtobufDecoder();

    @Override
    public Enctype enctype() {
        return Enctype.application_protobuf;
    }

    @Override
    public <T> T decode(Result rst, Class<T> clz) {
        try {
            return (T) ProtoBuf.der(rst.body());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void filter(FairyConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) {
        headers.put(Constants.h_serialization, Constants.at_protobuf);
    }
}
