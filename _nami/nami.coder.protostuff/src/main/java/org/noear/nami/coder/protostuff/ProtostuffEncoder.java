package org.noear.nami.coder.protostuff;

import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.ContentTypes;

/**
 * @author noear
 * @since 1.2
 */
public class ProtostuffEncoder implements Encoder {
    public static final ProtostuffEncoder instance = new ProtostuffEncoder();

    @Override
    public String enctype() {
        return ContentTypes.PROTOBUF_VALUE;
    }

    @Override
    public byte[] encode(Object obj) {
        return ProtostuffUtil.serialize(obj);
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
