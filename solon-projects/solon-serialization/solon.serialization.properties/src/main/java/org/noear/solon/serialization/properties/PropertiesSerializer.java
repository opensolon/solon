package org.noear.solon.serialization.properties;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.serialization.StringSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author noear
 * @since 2.7
 */
public class PropertiesSerializer implements StringSerializer {
    final Options options;

    public PropertiesSerializer(Options options) {
        this.options = options;
    }

    @Override
    public String serialize(Object obj) throws IOException {
        ONode oNode = ONode.loadObj(obj, options);
        Properties oProperties = oNode.toObject(Properties.class);

        StringBuilder buf = new StringBuilder();
        List<String> bufKeys = new ArrayList<>(oProperties.stringPropertyNames());
        bufKeys.sort(String::compareTo);

        for (String key : bufKeys) {
            buf.append(key).append("=").append(oProperties.getProperty(key)).append("\n");
        }

        return buf.toString();
    }
}
