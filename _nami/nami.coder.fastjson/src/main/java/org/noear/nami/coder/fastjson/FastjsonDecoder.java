package org.noear.nami.coder.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import org.noear.nami.NamiConfig;
import org.noear.nami.Decoder;
import org.noear.nami.common.Result;
import org.noear.nami.common.Constants;

import java.lang.reflect.Type;
import java.util.Map;

public class FastjsonDecoder implements Decoder {
    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

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

            if (str.contains("\"@type\"") && str.contains("Exception")) {
                returnVal = JSON.parseObject(str, Throwable.class);
            } else {
                returnVal = JSON.parseObject(str, type);
            }
        } catch (Throwable ex) {
            returnVal = ex;
        }

        if (returnVal != null && Throwable.class.isAssignableFrom(returnVal.getClass())) {
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
    public void filter(NamiConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) {
        headers.put(Constants.HEADER_SERIALIZATION, Constants.AT_TYPE_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.CONTENT_TYPE_JSON);
    }
}
