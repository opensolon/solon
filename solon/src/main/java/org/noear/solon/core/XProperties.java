package org.noear.solon.core;

import org.noear.solon.XUtil;

import java.util.Map;
import java.util.Properties;

public class XProperties extends Properties {
    /**获取某项配置*/
    public String get(String key) {
        return getProperty(key);
    }
    public String get(String key, String def) {
        return getProperty(key, def);
    }

    public int getInt(String key, int def) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return Integer.parseInt(temp);
        }
    }

    public long getLong(String key, long def) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return Long.parseLong(temp);
        }
    }

    public boolean getBool(String key, boolean def){
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return Boolean.parseBoolean(temp);
        }
    }

    public Object getRaw(String key){
        return super.get(key);
    }

    public XProperties getProp(String key) {
        XProperties prop = new XProperties();

        String key2 = key + ".";
        int idx2 = key2.length();

        String keyStr = null;
        for (Map.Entry<Object, Object> kv : this.entrySet()) {
            keyStr = kv.getKey().toString();
            if (keyStr.startsWith(key2)) {
                prop.put(keyStr.substring(idx2), kv.getValue());
            }
        }

        return prop;
    }

    public XMap getXmap(String key) {
        XMap map = new XMap();

        String key2 = key + ".";
        int idx2 = key2.length();

        String keyStr = null;
        for (Map.Entry<Object, Object> kv : this.entrySet()) {
            keyStr = kv.getKey().toString();
            if (keyStr.startsWith(key2)) {
                map.put(keyStr.substring(idx2), kv.getValue().toString());
            }
        }

        return map;
    }
}
