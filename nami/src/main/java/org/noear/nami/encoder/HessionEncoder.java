package org.noear.nami.encoder;

import com.caucho.hessian.io.Hessian2Output;
import org.noear.nami.Enctype;
import org.noear.nami.Encoder;
import java.io.ByteArrayOutputStream;

public class HessionEncoder implements Encoder {
    public static final HessionEncoder instance = new HessionEncoder();

    @Override
    public Enctype enctype() {
        return Enctype.application_hessian;
    }

    @Override
    public Object encode(Object obj) {
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
}
