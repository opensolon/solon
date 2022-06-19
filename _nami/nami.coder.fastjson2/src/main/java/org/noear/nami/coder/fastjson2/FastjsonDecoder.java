package org.noear.nami.coder.fastjson2;

import com.alibaba.fastjson2.JSON;
import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.Result;
import org.noear.nami.common.Constants;

import java.lang.reflect.Type;


/**
 * @author noear
 * @since 1.9
 */
public class FastjsonDecoder implements Decoder {

    public static final FastjsonDecoder instance = new FastjsonDecoder();


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

            if (str.contains("\"stackTrace\":[{")) {
                returnVal = JSON.parseObject(str, Throwable.class);
            } else {
                returnVal = JSON.parseObject(str, type);
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
