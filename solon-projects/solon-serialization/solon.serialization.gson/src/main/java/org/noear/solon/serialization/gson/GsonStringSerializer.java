package org.noear.solon.serialization.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;

/**
 * Gson 字符串序列化
 *
 * @author noear
 * @since 2.8
 */
public class GsonStringSerializer implements ContextSerializer<String> {
    private static final String label = "/json";

    private GsonBuilder config;

    public GsonBuilder getConfig() {
        if (config == null) {
            config = new GsonBuilder();
        }

        return config;
    }

    public void refresh() {
        _gson = getConfig().create();
    }

    private Gson _gson;

    public Gson getGson() {
        if (_gson == null) {
            Utils.locker().lock();

            try {
                if (_gson == null) {
                    _gson = config.create();
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return _gson;
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
        return "gson-json";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return getGson().toJson(obj);
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
        if (clz == null) {
            return JsonParser.parseString(data);
        } else {
            JsonElement jsonElement = JsonParser.parseString(data);
            return getGson().fromJson(jsonElement, clz);
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
            return JsonParser.parseString(data);
        } else {
            return null;
        }
    }
}
