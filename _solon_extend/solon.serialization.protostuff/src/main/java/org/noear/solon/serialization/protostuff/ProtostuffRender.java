package org.noear.solon.serialization.protostuff;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

import java.util.Base64;

/**
 * @author noear
 * @since 1.2
 */
public class ProtostuffRender implements Render {

    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        byte[] bytes = serializeDo(data);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "ProtostuffRender");
        }

        ctx.contentType("application/protobuf");
        ctx.output(serializeDo(obj));
    }

    private byte[] serializeDo(Object obj) throws Throwable {
        return ProtostuffUtil.serialize(obj);
    }
}
