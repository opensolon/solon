package org.noear.fairy.decoder;

import com.caucho.hessian.io.Hessian2Input;
import org.noear.fairy.Enctype;
import org.noear.fairy.IDecoder;
import org.noear.fairy.Result;

import java.io.ByteArrayInputStream;

public class HessionDecoder implements IDecoder {
    public static final HessionDecoder instance = new HessionDecoder();

    @Override
    public Enctype enctype() {
        return Enctype.application_hessian;
    }


    @Override
    public <T> T decode(Result rst, Class<T> clz) {
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
