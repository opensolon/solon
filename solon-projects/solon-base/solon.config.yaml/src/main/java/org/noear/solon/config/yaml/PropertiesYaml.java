package org.noear.solon.config.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesYaml extends Properties {
    private Yaml createYaml() {
        return new Yaml();
    }

    public synchronized void loadYml(InputStream inputStream) {
        Yaml yaml = createYaml();

        Object tmp = yaml.load(inputStream);

        String prefix = "";
        load0(prefix, tmp);
    }

    public synchronized void loadYml(Reader reader) throws IOException {
        Yaml yaml = createYaml();

        Object tmp = yaml.load(reader);

        String prefix = "";
        load0(prefix, tmp);
    }

    private void load0(String prefix, Object tmp) {
        if (tmp instanceof Map) {
            ((Map<String, Object>) tmp).forEach((k, v) -> {
                String prefix2 = prefix + "." + k;
                load0(prefix2, v);
            });
            return;
        }

        if (tmp instanceof List) {
            //do_put(prefix, tmp);

            int index = 0;
            for (Object v : ((List) tmp)) {
                String prefix2 = prefix + "[" + index + "]";
                load0(prefix2, v);
                index++;
            }
            return;
        }

        if (tmp == null) {
            put0(prefix, "");
        } else {
            put0(prefix, String.valueOf(tmp));
        }
    }

    private void put0(String key, Object val) {
        if (key.startsWith(".")) {
            put(key.substring(1), val);
        } else {
            put(key, val);
        }
    }
}
