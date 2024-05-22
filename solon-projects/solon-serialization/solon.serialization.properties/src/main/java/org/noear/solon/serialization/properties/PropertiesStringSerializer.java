package org.noear.solon.serialization.properties;

import org.noear.snack.ONode;
import org.noear.snack.core.NameValues;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ActionSerializer;

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
public class PropertiesStringSerializer implements ActionSerializer<String> {
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
    public Object deserializeBody(Context ctx) throws IOException {
        NameValues nameValues = new NameValues();
        for (Map.Entry<String, List<String>> kv : ctx.paramsMap().entrySet()) {
            for (String val : kv.getValue()) {
                nameValues.add(kv.getKey(), val);
            }
        }

        return ONode.loadObj(nameValues, getConfig());
    }
}
