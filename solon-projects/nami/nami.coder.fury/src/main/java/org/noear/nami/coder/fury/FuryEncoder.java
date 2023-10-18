package org.noear.nami.coder.fury;

import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import io.fury.Fury;
import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.ContentTypes;

import java.io.ByteArrayOutputStream;

/**
 * Hessian 编码器
 *
 * @author noear
 * @since 1.2
 * */
public class FuryEncoder implements Encoder {
    public static final FuryEncoder instance = new FuryEncoder();
    static Fury fury = Fury.builder().requireClassRegistration(false).build();

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
