package org.noear.solon.extend.yaml;

import org.noear.snack.ONode;

import java.io.IOException;
import java.util.Properties;

public class PropertiesJson extends Properties {

    public synchronized void loadJson(String text) throws IOException {
        ONode node = ONode.loadStr(text);

        String prefix = "";
        do_load(prefix, node);
    }

    private void do_load(String prefix, ONode tmp) {
        if (tmp.isObject()) {
            tmp.forEach((k, v) -> {
                String prefix2 = prefix + "." + k;
                do_load(prefix2, v);
            });
            return;
        }

        if (tmp.isArray()) {
            //do_put(prefix, tmp);

            int index = 0;
            for (ONode v : tmp.ary()) {
                String prefix2 = prefix + "[" + index + "]";
                do_load(prefix2, v);
                index++;
            }
            return;
        }

        if(tmp.isNull()){
            do_put(prefix, null);
        }else{
            do_put(prefix, tmp.getString());
        }
    }

    private void do_put(String key, Object val){
        if (key.startsWith(".")) {
            put(key.substring(1), val);
        } else {
            put(key, val);
        }
    }
}
