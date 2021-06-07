package org.noear.nami.coder.hession;

import com.caucho.hessian.io.Hessian2Input;
import org.noear.nami.Decoder;
import org.noear.nami.NamiContext;
import org.noear.nami.Result;
import org.noear.nami.common.Constants;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

public class HessianDecoder implements Decoder {
    public static final HessianDecoder instance = new HessianDecoder();

    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_HESSIAN;
    }


    @Override
    public <T> T decode(Result rst, Type type) {
        Hessian2Input hi = new Hessian2Input(new ByteArrayInputStream(rst.body()));

        Object returnVal = null;
        try {
            if (rst.body().length == 0) {
                return null;
            }

            returnVal = hi.readObject();
        } catch (Throwable ex) {
            returnVal = ex;
        }

        if (returnVal != null && returnVal instanceof Throwable) {
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
    public void pretreatment(NamiContext ctx) {
        ctx.headers.put(Constants.HEADER_SERIALIZATION, Constants.AT_HESSION);
        ctx.headers.put(Constants.HEADER_ACCEPT, Constants.CONTENT_TYPE_HESSIAN);
    }
}
