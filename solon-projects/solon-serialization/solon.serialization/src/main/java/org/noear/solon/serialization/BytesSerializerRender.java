package org.noear.solon.serialization;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.serialize.Serializer;

import java.util.Base64;

/**
 * 字节组序列化渲染器
 *
 * @author noear
 * @since 2.8
 */
public class BytesSerializerRender implements Render {
    private Serializer<byte[]> serializer;
    private String contentType;

    public BytesSerializerRender(Serializer<byte[]> serializer, String contentType) {
        this.serializer = serializer;
        this.contentType = contentType;
    }

    public Serializer<byte[]> getSerializer() {
        return serializer;
    }


    @Override
    public String getName() {
        return this.getClass().getSimpleName() + "#" + serializer.name();
    }

    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        byte[] tmp = serializer.serialize(data);
        return Base64.getEncoder().encodeToString(tmp);
    }

    /**
     * 渲染
     */
    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (SerializationConfig.isOutputMeta()) {
            ctx.headerAdd("solon.serialization", getName());
        }

        ctx.contentType(contentType);

        if (obj instanceof ModelAndView) {
            ctx.output(serializer.serialize(((ModelAndView) obj).model()));
        } else {
            ctx.output(serializer.serialize(obj));
        }
    }
}
