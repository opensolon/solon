package org.noear.mlog;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 元信息
 *
 * @author noear
 * @since 1.2
 */
public class Metainfo {
    Map<String, String> data = new LinkedHashMap<>();

    public String get(String key) {
        return data.get(key);
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public void remove(String key) {
        data.remove(key);
    }

    public Set<String> allKeys() {
        return data.keySet();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        data.forEach((k, v) -> {
            buf.append("[").append(k).append(":").append(v).append("]");
        });

        return buf.toString();
    }
}
