package org.noear.nami.coder.protostuff;

import org.noear.nami.Decoder;
import org.noear.nami.Context;
import org.noear.nami.common.Constants;
import org.noear.nami.Result;

import java.lang.reflect.Type;

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
    public <T> T decode(Result rst, Type type) {
        Object returnVal = null;
        try {
            if (rst.body().length == 0) {
                return null;
            }

            returnVal = ProtostuffUtil.deserialize(rst.body());

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
        ctx.headers.put(Constants.HEADER_SERIALIZATION, Constants.AT_PROTOBUF);
        ctx.headers.put(Constants.HEADER_ACCEPT, Constants.CONTENT_TYPE_PROTOBUF);
    }
}
