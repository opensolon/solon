package org.noear.solonclient.serializer;

import org.noear.snack.ONode;
import org.noear.solonclient.Enctype;
import org.noear.solonclient.IDeserializer;
import org.noear.solonclient.ISerializer;
import org.noear.solonclient.Result;

public class SnackSerializerD implements ISerializer, IDeserializer {
    public static final SnackSerializerD instance = new SnackSerializerD(false);
    public static final SnackSerializerD instance_type = new SnackSerializerD(true);

    private boolean usingType;
    public SnackSerializerD(boolean usingType){
        this.usingType = usingType;
    }

    @Override
    public Enctype enctype() {
        return Enctype.application_json;
    }

    @Override
    public Object serialize(Object obj) {
        if (usingType) {
            return ONode.serialize(obj);
        } else {
            return ONode.stringify(obj);
        }
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
