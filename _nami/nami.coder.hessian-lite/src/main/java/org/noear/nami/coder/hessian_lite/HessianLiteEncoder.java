package org.noear.nami.coder.hessian_lite;

import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.Constants;

import java.io.ByteArrayOutputStream;

/**
 * HessianLite 编码器
 *
 * @author noear
 * @since 1.10
 * */
public class HessianLiteEncoder implements Encoder {
    public static final HessianLiteEncoder instance = new HessianLiteEncoder();

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
    public void pretreatment(Context ctx) {

    }
}
