package org.noear.solon.serialization.hessian;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ActionSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author noear
 * @since 2.8
 */
public class HessianBytesSerializer implements ActionSerializer<byte[]> {
    @Override
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Hessian2Output ho = new Hessian2Output(out);
        ho.writeObject(obj);
        ho.close();

        return out.toByteArray();
    }

    @Override
    public String name() {
        return "hessian-bytes";
    }

    @Override
    public Object deserialize(byte[] data, Class<?> clz) throws IOException {
        Hessian2Input hi = new Hessian2Input(new ByteArrayInputStream(data));
        return hi.readObject();
    }

    @Override
    public Object deserializeBody(Context ctx) throws IOException {
        Hessian2Input hi = new Hessian2Input(ctx.bodyAsStream());
        return hi.readObject();
    }
}
