package org.noear.fairy.decoder;

import org.noear.fairy.Enctype;
import org.noear.fairy.FairyConfig;
import org.noear.fairy.IDecoder;
import org.noear.fairy.Result;
import org.noear.fairy.channel.Constants;
import org.noear.snack.ONode;

import java.util.Map;

public class SnackDecoder implements IDecoder {
    public static final SnackDecoder instance = new SnackDecoder();

    @Override
    public Enctype enctype() {
        return Enctype.application_json;
    }


    @Override
    public <T> T decode(Result rst, Class<T> clz) {
        String str = rst.bodyAsString();

        Object returnVal = null;
        try {
            if (str == null) {
                return (T) str;
            }
            returnVal = ONode.deserialize(str, clz);

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
    public void filter(FairyConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) {
        headers.put(Constants.h_serialization, Constants.at_type_json);
    }
}
