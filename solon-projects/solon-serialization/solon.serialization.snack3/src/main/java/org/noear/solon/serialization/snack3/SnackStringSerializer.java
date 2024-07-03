package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Json 序列化器
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class SnackStringSerializer implements ContextSerializer<String> {
    private static final String label = "/json";

    private Options config;

    public Options getConfig() {
        if (config == null) {
            config = Options.def();
        }

        return config;
    }

    public void setConfig(Options config) {
        if (config != null) {
            this.config = config;
        }
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.contains(label);
        }
    }

    @Override
    public String name() {
        return "snack3-json";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return ONode.loadObj(obj, getConfig()).toJson();
    }

    @Override
    public Object deserialize(String data, Type toType) throws IOException {
        if (toType == null) {
            return ONode.loadStr(data, getConfig());
        } else {
            return ONode.loadStr(data, getConfig()).toObject(toType);
        }
    }

    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {
        ctx.contentType(getContentType());

        if (data instanceof ModelAndView) {
            ctx.output(serialize(((ModelAndView) data).model()));
        } else {
            ctx.output(serialize(data));
        }
    }

    @Override
    public Object deserializeFromBody(Context ctx) throws IOException {
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return ONode.loadStr(data, getConfig());
        } else {
            return null;
        }
    }
}
