package org.noear.nami.coder.protostuff;

import org.noear.nami.Encoder;
import org.noear.nami.Context;
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
        return ProtostuffUtil.serialize(obj);
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
