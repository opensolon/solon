package org.noear.solon.extend.properties.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesYaml extends Properties {
    public synchronized void loadYml(InputStream inputStream) {
        Yaml yaml = new Yaml();

        Object tmp = yaml.load(inputStream);

        String prefix = "";
        do_load(prefix, tmp);
    }

    public synchronized void loadYml(Reader reader) throws IOException {
        Yaml yaml = new Yaml();

        Object tmp = yaml.load(reader);

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
            //do_put(prefix, tmp);

            int index = 0;
            for (Object v : ((List) tmp)) {
                String prefix2 = prefix + "[" + index + "]";
                do_load(prefix2, v);
                index++;
            }
            return;
        }

        do_put(prefix, String.valueOf(tmp));
    }

    private void do_put(String key, Object val){
        if (key.startsWith(".")) {
            put(key.substring(1), val);
        } else {
            put(key, val);
        }
    }
}
