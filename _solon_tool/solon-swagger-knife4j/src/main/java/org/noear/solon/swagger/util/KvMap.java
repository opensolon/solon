package org.noear.solon.swagger.util;

import org.noear.solon.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2022/4/13 created
 */

public class KvMap extends HashMap {
    public KvMap() {
    }

    public static KvMap by(Object key, Object value) {
        return (new KvMap()).set(key, value);
    }

    public static KvMap create() {
        return new KvMap();
    }

    public KvMap set(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    public KvMap setIfNotBlank(Object key, String value) {
        if (Utils.isNotBlank(value)) {
            this.set(key, value);
        }

        return this;
    }

    public KvMap setIfNotNull(Object key, Object value) {
        if (value != null) {
            this.set(key, value);
        }

        return this;
    }

    public KvMap set(Map map) {
        super.putAll(map);
        return this;
    }

    public KvMap set(KvMap kv) {
        super.putAll(kv);
        return this;
    }

    public KvMap delete(Object key) {
        super.remove(key);
        return this;
    }

    public <T> T getAs(Object key) {
        return (T)this.get(key);
    }

    public String getStr(Object key) {
        Object s = this.get(key);
        return s != null ? s.toString() : null;
    }

    public Integer getInt(Object key) {
        Number n = (Number)this.get(key);
        return n != null ? n.intValue() : null;
    }

    public Long getLong(Object key) {
        Number n = (Number)this.get(key);
        return n != null ? n.longValue() : null;
    }

    public Double getDouble(Object key) {
        Number n = (Number)this.get(key);
        return n != null ? n.doubleValue() : null;
    }

    public Float getFloat(Object key) {
        Number n = (Number)this.get(key);
        return n != null ? n.floatValue() : null;
    }

    public Number getNumber(Object key) {
        return (Number)this.get(key);
    }

    public Boolean getBoolean(Object key) {
        return (Boolean)this.get(key);
    }

    public boolean notNull(Object key) {
        return this.get(key) != null;
    }

    public boolean isNull(Object key) {
        return this.get(key) == null;
    }

    public boolean isTrue(Object key) {
        Object value = this.get(key);
        return value instanceof Boolean && (Boolean)value;
    }

    public boolean isFalse(Object key) {
        Object value = this.get(key);
        return value instanceof Boolean && !(Boolean)value;
    }

    public boolean equals(Object kv) {
        return kv instanceof KvMap && super.equals(kv);
    }
}
