package org.noear.solon.serialization.sbe;

import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author noear
 * @since 3.0
 */
public class SbeBytesSerializer implements ContextSerializer<byte[]> {
    private static final String label = "application/sbe";
    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.startsWith(label);
        }
    }

    @Override
    public String mimeType() {
        return label;
    }

    @Override
    public Class<byte[]> dataType() {
        return byte[].class;
    }

    @Override
    public byte[] serialize(Object fromObj) throws IOException {
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] data, Type toType) throws IOException {
        return null;
    }

    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {

    }

    @Override
    public Object deserializeFromBody(Context ctx) throws IOException {
        return null;
    }
}
