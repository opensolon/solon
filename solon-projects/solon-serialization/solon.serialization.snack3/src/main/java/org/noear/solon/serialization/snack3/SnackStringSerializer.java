package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ActionSerializer;

import java.io.IOException;

/**
 * Json 序列化器
 *
 * @author noear
 * @since 1.5
 */
public class SnackStringSerializer implements ActionSerializer<String> {
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
    public String name() {
        return "snack3-json";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return ONode.loadObj(obj, getConfig()).toJson();
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
        if (clz == null) {
            return ONode.loadStr(data, getConfig());
        } else {
            return ONode.loadStr(data, getConfig()).toObject(clz);
        }
    }

    @Override
    public Object deserializeBody(Context ctx) throws IOException {
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return ONode.loadStr(data, getConfig());
        } else {
            return null;
        }
    }
}
