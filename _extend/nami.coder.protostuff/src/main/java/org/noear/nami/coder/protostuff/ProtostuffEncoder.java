package org.noear.nami.coder.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.noear.nami.Encoder;
import org.noear.nami.common.Constants;

/**
 * @author noear
 * @since 1.2
 */
public class ProtostuffEncoder implements Encoder {
    public static final ProtostuffEncoder instance = new ProtostuffEncoder();

    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_PROTOBUF;
    }

    @Override
    public byte[] encode(Object obj) {
        Schema schema = RuntimeSchema.getSchema(obj.getClass());
        return ProtobufIOUtil.toByteArray(obj, schema, LinkedBuffer.allocate());
    }
}
