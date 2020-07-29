package org.noear.solonclient.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.noear.solonclient.IDeserializer;
import org.noear.solonclient.ISerializer;
import org.noear.solonclient.Result;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class HessionSerializer implements ISerializer, IDeserializer {
    public static final HessionSerializer instance = new HessionSerializer();

    @Override
    public Object serialize(Object obj) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(out);

        try {
            ho.writeObject(obj);
            ho.getBytesOutputStream().flush();
            ho.completeMessage();
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
            if (clz == Void.TYPE) {
                return (T) hi.readObject();
            } else {
                return (T) hi.readObject(clz);
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
