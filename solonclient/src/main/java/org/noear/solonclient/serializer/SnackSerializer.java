package org.noear.solonclient.serializer;

import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;
import org.noear.solonclient.ISerializer;

public class SnackSerializer implements ISerializer {
    public static final SnackSerializer instance = new SnackSerializer();

    @Override
    public String stringify(Object obj) {
        return ONode.stringify(obj);
    }

    @Override
    public String serialize(Object obj) {
        return ONode.serialize(obj);
    }

    @Override
    public <T> T deserialize(String str, Class<T> clz) {
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
