package org.noear.solon.serialization.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

/**
 * @author noear 2021/1/17 created
 */
public class ProtostuffRender implements Render {
    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "ProtostuffRender");
        }

        ctx.contentType("application/protobuf");
        ctx.output(serializeDo(obj));
    }

    private byte[] serializeDo(Object obj) throws Throwable {
        Schema schema = RuntimeSchema.getSchema(obj.getClass());
        return ProtobufIOUtil.toByteArray(obj, schema, LinkedBuffer.allocate());
    }
}
