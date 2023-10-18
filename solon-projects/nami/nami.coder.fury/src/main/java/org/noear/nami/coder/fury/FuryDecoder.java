package org.noear.nami.coder.fury;

import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.Result;
import org.noear.nami.common.Constants;
import org.noear.nami.common.ContentTypes;

import java.lang.reflect.Type;

/**
 * Fury 解码器
 *
 * @author noear
 * @since 2.5
 * */
public class FuryDecoder implements Decoder {
    public static final FuryDecoder instance = new FuryDecoder();

    @Override
    public String enctype() {
        return ContentTypes.HESSIAN_VALUE;
    }


    @Override
    public <T> T decode(Result rst, Type type) {
        Object returnVal = null;
        try {
            if (rst.body().length == 0) {
                return null;
            }

            returnVal = FuryUtil.fury.deserialize(rst.body());
        } catch (Throwable ex) {
            returnVal = ex;
        }

        if (returnVal != null && returnVal instanceof Throwable) {
            if (returnVal instanceof RuntimeException) {
                throw (RuntimeException) returnVal;
            } else {
                throw new RuntimeException((Throwable) returnVal);
            }
        } else {
            return (T) returnVal;
        }
    }

    @Override
    public void pretreatment(Context ctx) {
        ctx.headers.put(Constants.HEADER_SERIALIZATION, Constants.AT_FURY);
        ctx.headers.put(Constants.HEADER_ACCEPT, ContentTypes.FURY_VALUE);
    }
}
