package org.noear.solon.serialization.fury;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.SerializationConfig;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 2.5
 * */
public class FuryRender implements Render {
    private final FuryBytesSerializer serializer = new FuryBytesSerializer();

    @Override
    public boolean matched(Context ctx, String accept) {
        if (accept == null) {
            return false;
        } else {
            return accept.startsWith(FuryActionExecutor.label);
        }
    }

    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        byte[] bytes = null;
        if (data instanceof ModelAndView) {
            bytes = serializeDo(((ModelAndView) data).model());
        } else {
            bytes = serializeDo(data);
        }

        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (SerializationConfig.isOutputMeta()) {
            ctx.headerAdd("solon.serialization", "FuryRender");
        }

        ctx.contentType("application/fury");

        if (obj instanceof ModelAndView) {
            ctx.output(serializeDo(((ModelAndView) obj).model()));
        } else {
            ctx.output(serializeDo(obj));
        }
    }

    private byte[] serializeDo(Object obj) throws Throwable {
        return serializer.serialize(obj);
    }
}
