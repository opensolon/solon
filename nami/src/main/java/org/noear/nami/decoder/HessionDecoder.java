package org.noear.nami.decoder;

import com.caucho.hessian.io.Hessian2Input;
import org.noear.nami.Enctype;
import org.noear.nami.NamiConfig;
import org.noear.nami.Decoder;
import org.noear.nami.Result;
import org.noear.nami.channel.Constants;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class HessionDecoder implements Decoder {
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

    @Override
    public void filter(NamiConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) {
        headers.put(Constants.h_serialization, Constants.at_hession);
    }
}
