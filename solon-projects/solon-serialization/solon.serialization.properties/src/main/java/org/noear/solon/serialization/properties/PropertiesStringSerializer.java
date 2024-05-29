package org.noear.solon.serialization.properties;

import org.noear.snack.ONode;
import org.noear.snack.core.NameValues;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author noear
 * @since 2.7
 * @since 2.8
 */
public class PropertiesStringSerializer implements ContextSerializer<String> {
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
        return "application/properties";
    }

    @Override
    public boolean matched(Context ctx, String mime) {
        return false;
    }

    @Override
    public String name() {
        return "properties-string";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        ONode oNode = ONode.loadObj(obj, getConfig());
        Properties oProperties = oNode.toObject(Properties.class);

        StringBuilder buf = new StringBuilder();
        List<String> bufKeys = new ArrayList<>(oProperties.stringPropertyNames());
        bufKeys.sort(String::compareTo);

        for (String key : bufKeys) {
            buf.append(key).append("=").append(oProperties.getProperty(key)).append("\n");
        }

        return buf.toString();
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
        Properties prop = Utils.buildProperties(data);

        if (clz == null) {
            return prop;
        } else {
            ONode oNode = ONode.loadObj(prop, getConfig());
            return oNode.toObject(clz);
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
        NameValues nameValues = new NameValues();
        for (Map.Entry<String, List<String>> kv : ctx.paramsMap().entrySet()) {
            for (String val : kv.getValue()) {
                nameValues.add(kv.getKey(), val);
            }
        }

        return ONode.loadObj(nameValues, getConfig());
    }
}
