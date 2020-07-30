package org.noear.solonclient.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.noear.solonclient.Enctype;
import org.noear.solonclient.IDeserializer;
import org.noear.solonclient.ISerializer;
import org.noear.solonclient.Result;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class HessionSerializerD implements ISerializer, IDeserializer {
    public static final HessionSerializerD instance = new HessionSerializerD();

    @Override
    public Enctype enctype() {
        return Enctype.application_hessian;
    }

    @Override
    public Object serialize(Object obj) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(out);

        try {
            ho.writeObject(obj);
            ho.close();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        return out.toByteArray();
    }

    @Override
    public <T> T deserialize(Result rst, Class<T> clz) {
        Hessian2Input hi = new Hessian2Input(new ByteArrayInputStream(rst.body()));

        try {
            if (Void.TYPE == clz) {
                return null;
            } else {
                return (T) hi.readObject();
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
