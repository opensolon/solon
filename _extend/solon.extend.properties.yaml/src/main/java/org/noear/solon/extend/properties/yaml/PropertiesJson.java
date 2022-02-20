package org.noear.solon.extend.properties.yaml;

import org.noear.snack.ONode;

import java.io.IOException;
import java.util.Properties;

public class PropertiesJson extends Properties {

    public synchronized void loadJson(String text) throws IOException {
        ONode node = ONode.loadStr(text);

        String prefix = "";
        load0(prefix, node);
    }

    private void load0(String prefix, ONode tmp) {
        if (tmp.isObject()) {
            tmp.forEach((k, v) -> {
                String prefix2 = prefix + "." + k;
                load0(prefix2, v);
            });
            return;
        }

        if (tmp.isArray()) {
            //do_put(prefix, tmp);

            int index = 0;
            for (ONode v : tmp.ary()) {
                String prefix2 = prefix + "[" + index + "]";
                load0(prefix2, v);
                index++;
            }
            return;
        }

        if(tmp.isNull()){
            put0(prefix, "");
        }else{
            put0(prefix, tmp.getString());
        }
    }

    private void put0(String key, Object val){
        if (key.startsWith(".")) {
            put(key.substring(1), val);
        } else {
            put(key, val);
        }
    }
}
