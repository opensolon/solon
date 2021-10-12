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

    public int getInt(String field) {
        String tmp = get(field);
        return tmp == null ? 0 : Integer.parseInt(tmp);
    }

    public long getLong(String field) {
        String tmp = get(field);
        return tmp == null ? 0L : Long.parseLong(tmp);
    }

    public float getFloat(String field) {
        String tmp = get(field);
        return tmp == null ? 0 : Float.parseFloat(tmp);
    }

    public double getDouble(String field) {
        String tmp = get(field);
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
    public boolean containsKey(Object field) {
        return _map.containsKey(field);
    }

    @Override
    public boolean containsValue(Object value) {
        return _map.containsValue(value);
    }

    @Override
    public String get(Object field) {
        return _map.get(field);
    }

    @Override
    public String put(String field, String value) {
        return _map.put(field,value);
    }

    @Override
    public String remove(Object field) {
        return _map.remove(field);
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
