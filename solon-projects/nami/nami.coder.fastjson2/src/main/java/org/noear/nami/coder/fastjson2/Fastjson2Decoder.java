package org.noear.nami.coder.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.EncoderTyped;
import org.noear.nami.Result;
import org.noear.nami.common.Constants;
import org.noear.nami.common.ContentTypes;

import java.lang.reflect.Type;


/**
 * @author noear
 * @since 1.9
 */
public class Fastjson2Decoder implements Decoder {

    public static final Fastjson2Decoder instance = new Fastjson2Decoder();


    @Override
    public String enctype() {
        return ContentTypes.JSON_VALUE;
    }

    @Override
    public <T> T decode(Result rst, Type type) {
        String str = rst.bodyAsString();

        Object returnVal = null;
        try {
            if (str == null) {
                return (T) str;
            }

            if ("null".equals(str)) {
                return null;
            }

            if (str.contains("\"stackTrace\":[{")) {
                returnVal = JSON.parseObject(str, Throwable.class, JSONReader.Feature.SupportAutoType);
            } else {
                returnVal = JSON.parseObject(str, type, JSONReader.Feature.SupportAutoType);
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
        if (ctx.config.getEncoder() instanceof EncoderTyped) {
            ctx.headers.put(Constants.HEADER_SERIALIZATION, Constants.AT_TYPE_JSON);
        }

        ctx.headers.put(Constants.HEADER_ACCEPT, ContentTypes.JSON_VALUE);
    }
}
