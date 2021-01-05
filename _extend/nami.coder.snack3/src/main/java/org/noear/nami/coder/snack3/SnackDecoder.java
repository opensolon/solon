package org.noear.nami.coder.snack3;

import org.noear.nami.NamiConfig;
import org.noear.nami.Decoder;
import org.noear.nami.common.Result;
import org.noear.nami.common.Constants;
import org.noear.snack.ONode;

import java.lang.reflect.Type;
import java.util.Map;

public class SnackDecoder implements Decoder {
    public static final SnackDecoder instance = new SnackDecoder();

    @Override
    public String enctype() {
        return Constants.ct_json;
    }

    @Override
    public <T> T decode(Result rst, Type type) {
        String str = rst.bodyAsString();

        Object returnVal = null;
        try {
            if (str == null) {
                return (T) str;
            }
            returnVal = ONode.deserialize(str, type);

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
        headers.put(Constants.h_serialization, Constants.at_type_json);
        headers.put(Constants.h_accept, Constants.ct_json);
    }
}
