package org.noear.nami.coder.hessian;

import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.ContentTypes;

import java.io.ByteArrayOutputStream;

/**
 * Hessian 编码器
 *
 * @author noear
 * @since 1.2
 * */
public class HessianEncoder implements Encoder {
    public static final HessianEncoder instance = new HessianEncoder();

    @Override
    public String enctype() {
        return ContentTypes.HESSIAN_VALUE;
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
    public void pretreatment(Context ctx) {

    }
}
