package org.noear.solon.extend.properties.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesYaml extends Properties {
    public synchronized void loadYml(InputStream inputStream) throws IOException {
        Yaml yaml = new Yaml();

        Object tmp = yaml.load(inputStream);

        String prefix = "";
        do_load(prefix, tmp);
    }

    private void do_load(String prefix, Object tmp) {
        if (tmp instanceof Map) {
            ((Map<String, Object>) tmp).forEach((k, v) -> {
                String prefix2 = prefix + "." + k;
                do_load(prefix2, v);
            });
            return;
        }

        if (tmp instanceof List) {
            put(prefix,tmp);
            return;
        }

        if (prefix.startsWith(".")) {
            setProperty(prefix.substring(1), String.valueOf(tmp));
        } else {
            setProperty(prefix, String.valueOf(tmp));
        }
    }
}
