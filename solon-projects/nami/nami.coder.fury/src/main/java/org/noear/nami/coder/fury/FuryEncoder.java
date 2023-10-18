package org.noear.nami.coder.fury;

import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.ContentTypes;

/**
 * Hessian 编码器
 *
 * @author noear
 * @since 1.2
 * */
public class FuryEncoder implements Encoder {
    public static final FuryEncoder instance = new FuryEncoder();

    @Override
    public String enctype() {
        return ContentTypes.FURY_VALUE;
    }

    @Override
    public byte[] encode(Object obj) {
       return FuryUtil.fury.serialize(obj);
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
