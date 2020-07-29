package org.noear.solonclient.serializer;

import org.noear.snack.ONode;
import org.noear.solonclient.IDeserializer;
import org.noear.solonclient.ISerializer;
import org.noear.solonclient.Result;

public class SnackSerializer implements ISerializer, IDeserializer {
    public static final SnackSerializer instance = new SnackSerializer();

    @Override
    public Object serialize(Object obj) {
        return ONode.stringify(obj);

//        return ONode.serialize(obj);
    }

    @Override
    public <T> T deserialize(Result rst, Class<T> clz) {
        String str = rst.bodyAsString();

        Object returnVal = null;
        try {
            if (str == null) {
                return (T) str;
            }
            returnVal = ONode.deserialize(str, clz);

        } catch (Throwable ex) {
            System.err.println("error::" + str);
            returnVal = ex;
        }

        if (returnVal != null && Throwable.class.isAssignableFrom(returnVal.getClass())) {
            throw new RuntimeException((Throwable) returnVal);
        } else {
            return (T) returnVal;
        }
    }
}
