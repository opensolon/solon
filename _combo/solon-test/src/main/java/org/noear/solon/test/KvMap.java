package org.noear.solon.test;

import java.util.LinkedHashMap;

/**
 * @author noear
 * @since 1.3
 */
public class KvMap extends LinkedHashMap<String,Object> {
    public KvMap set(String key, Object val) {
        put(key, val);
        return this;
    }
}
