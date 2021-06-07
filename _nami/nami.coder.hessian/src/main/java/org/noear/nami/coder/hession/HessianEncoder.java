package org.noear.nami.coder.hession;

import com.caucho.hessian.io.Hessian2Output;
import org.noear.nami.Encoder;
import org.noear.nami.NamiContext;
import org.noear.nami.common.Constants;

import java.io.ByteArrayOutputStream;

public class HessianEncoder implements Encoder {
    public static final HessianEncoder instance = new HessianEncoder();

    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_HESSIAN;
    }

    @Override
    public byte[] encode(Object obj) {
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
    public void pretreatment(NamiContext ctx) {

    }
}
