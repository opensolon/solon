package org.noear.nami.coder.hession;

import com.caucho.hessian.io.Hessian2Input;
import org.noear.nami.NamiConfig;
import org.noear.nami.Decoder;
import org.noear.nami.common.Result;
import org.noear.nami.common.Constants;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.Map;

public class HessianDecoder implements Decoder {
    public static final HessianDecoder instance = new HessianDecoder();

    @Override
    public String enctype() {
        return Constants.ct_hessian;
    }


    @Override
    public <T> T decode(Result rst, Type type) {
        Hessian2Input hi = new Hessian2Input(new ByteArrayInputStream(rst.body()));

        try {
            if (Void.TYPE == type) {
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

    @Override
    public void filter(NamiConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) {
        headers.put(Constants.h_serialization, Constants.at_hession);
        headers.put(Constants.h_accept, Constants.ct_hessian);
    }
}
