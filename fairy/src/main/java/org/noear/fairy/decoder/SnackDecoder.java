package org.noear.fairy.decoder;

import org.noear.fairy.Enctype;
import org.noear.fairy.IDecoder;
import org.noear.fairy.Result;
import org.noear.snack.ONode;

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
}
