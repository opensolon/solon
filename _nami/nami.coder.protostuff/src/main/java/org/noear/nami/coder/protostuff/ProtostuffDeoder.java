package org.noear.nami.coder.protostuff;

import org.noear.nami.Decoder;
import org.noear.nami.NamiConfig;
import org.noear.nami.common.Constants;
import org.noear.nami.common.Result;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author noear
 * @since 1.2
 */
public class ProtostuffDeoder implements Decoder {
    public static final ProtostuffDeoder instance = new ProtostuffDeoder();


    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_PROTOBUF;
    }

    @Override
    public <T> T decode(Result rst, Type clz) {
        return ProtostuffUtil.deserialize(rst.body());
    }

    @Override
    public void filter(NamiConfig cfg, String action, String url, Map<String, String> headers, Map<String, Object> args) {
        headers.put(Constants.HEADER_SERIALIZATION, Constants.AT_PROTOBUF);
        headers.put(Constants.HEADER_ACCEPT, Constants.CONTENT_TYPE_PROTOBUF);
    }
}
