package org.noear.solonclient.serializer;

import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;
import org.noear.solonclient.ISerializer;

public class SnackSerializer implements ISerializer {
    public static final SnackSerializer instance = new SnackSerializer();

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

        } catch (Exception ex) {
            System.out.println(str);
            returnVal = ex;
        }

        if (returnVal != null && Throwable.class.isAssignableFrom(returnVal.getClass())) {
            throw new RuntimeException((Throwable) returnVal);
        } else {
            return (T) returnVal;
        }
    }
}
