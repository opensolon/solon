package org.noear.nami.coder.fury;

import io.fury.Fury;
import io.fury.ThreadSafeFury;
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
    static ThreadSafeFury fury = Fury.builder().requireClassRegistration(false).buildThreadSafeFury();

    @Override
    public String enctype() {
        return ContentTypes.FURY_VALUE;
    }

    @Override
    public byte[] encode(Object obj) {
       return fury.serialize(obj);
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
