package org.noear.solon.extend.redisx.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Redis Hash map
 *
 * @author noear
 * @since 1.5
 * */
public class RedisHashMap implements Map<String,String> {
    private Map<String,String> _map;
    public RedisHashMap(Map<String,String> map){
        _map = map;
    }

    public int getInt(String key) {
        String tmp = get(key);
        return tmp == null ? 0 : Integer.parseInt(tmp);
    }

    public long getLong(String key) {
        String tmp = get(key);
        return tmp == null ? 0L : Long.parseLong(tmp);
    }

    public float getFloat(String key) {
        String tmp = get(key);
        return tmp == null ? 0 : Float.parseFloat(tmp);
    }

    public double getDouble(String key) {
        String tmp = get(key);
        return tmp == null ? 0 : Double.parseDouble(tmp);
    }

    @Override
    public int size() {
        return _map.size();
    }

    @Override
    public boolean isEmpty() {
        return _map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return _map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return _map.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return _map.get(key);
    }

    @Override
    public String put(String key, String value) {
        return _map.put(key,value);
    }

    @Override
    public String remove(Object key) {
        return _map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        _map.putAll(m);
    }

    @Override
    public void clear() {
        _map.clear();
    }

    @Override
    public Set<String> keySet() {
        return _map.keySet();
    }

    @Override
    public Collection<String> values() {
        return _map.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return _map.entrySet();
    }
}
