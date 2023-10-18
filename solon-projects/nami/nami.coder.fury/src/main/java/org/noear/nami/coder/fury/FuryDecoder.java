package org.noear.nami.coder.fury;

import io.fury.Fury;
import io.fury.ThreadSafeFury;
import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.Result;
import org.noear.nami.common.Constants;
import org.noear.nami.common.ContentTypes;

import java.lang.reflect.Type;

/**
 * Hessian 解码器
 *
 * @author noear
 * @since 1.2
 * */
public class FuryDecoder implements Decoder {
    public static final FuryDecoder instance = new FuryDecoder();
    static ThreadSafeFury fury = Fury.builder().requireClassRegistration(false).buildThreadSafeFury();

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

            returnVal = fury.deserialize(rst.body());
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
