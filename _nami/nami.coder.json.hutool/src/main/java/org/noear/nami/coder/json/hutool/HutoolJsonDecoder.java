package org.noear.nami.coder.json.hutool;

import cn.hutool.json.JSONUtil;
import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.Result;
import org.noear.nami.common.Constants;

import java.lang.reflect.Type;

/**
 * @author noear
 * @since 1.5
 */
public class HutoolJsonDecoder implements Decoder {
    public static final HutoolJsonDecoder instance = new HutoolJsonDecoder();


    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_JSON;
    }

    @Override
    public <T> T decode(Result rst, Type type) {
        String str = rst.bodyAsString();

        Object returnVal = null;
        try {
            if (str == null) {
                return (T) str;
            }

            if (str.contains("\"stackTrace\":[")) {
                returnVal = JSONUtil.toBean(str, Throwable.class);
            } else {
                returnVal = JSONUtil.toBean(str, type, false);
            }
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
        ctx.headers.put(Constants.HEADER_SERIALIZATION, Constants.AT_TYPE_JSON);
        ctx.headers.put(Constants.HEADER_ACCEPT, Constants.CONTENT_TYPE_JSON);
    }
}
