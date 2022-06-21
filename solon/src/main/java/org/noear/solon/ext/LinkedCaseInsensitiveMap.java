package org.noear.solon.ext;

import org.noear.solon.core.NvMap;

import java.io.Serializable;
import java.util.*;

/**
 * 忽略大小写的LinkedMap
 *
 * @see NvMap
 * @author noear
 * @since 1.0
 * */
@SuppressWarnings("serial")
public class LinkedCaseInsensitiveMap<V> implements Map<String, V>, Serializable, Cloneable {

    private final LinkedHashMap<String, V> _m;
    private final HashMap<String, String> _k;
    private final Locale locale;

    public LinkedCaseInsensitiveMap() {
        this(16, null);
    }

    public LinkedCaseInsensitiveMap(int initialCapacity, Locale locale) {
        this._m = new LinkedHashMap<String, V>(initialCapacity) {
            @Override
            public boolean containsKey(Object key) {
                return LinkedCaseInsensitiveMap.this.containsKey(key);
            }

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
                boolean doRemove = LinkedCaseInsensitiveMap.this.removeEldestEntry(eldest);
                if (doRemove) {
                    _k.remove(convertKey(eldest.getKey()));
                }
                return doRemove;
            }
        };
        this._k = new HashMap<>(initialCapacity);
        this.locale = (locale != null ? locale : Locale.getDefault());
    }

    /**
     * Copy constructor.
     */
    @SuppressWarnings("unchecked")
    private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
        this._m = (LinkedHashMap<String, V>) other._m.clone();
        this._k = (HashMap<String, String>) other._k.clone();
        this.locale = other.locale;
    }


    // Implementation of java.util.Map

    @Override
    public int size() {
        return this._m.size();
    }

    @Override
    public boolean isEmpty() {
        return this._m.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String && this._k.containsKey(convertKey((String) key)));
    }

    @Override
    public boolean containsValue(Object value) {
        return this._m.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            String key2 = this._k.get(convertKey((String) key));
            if (key2 != null) {
                return this._m.get(key2);
            }
        }
        return null;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (key instanceof String) {
            String key2 = this._k.get(convertKey((String) key));
            if (key2 != null) {
                return this._m.get(key2);
            }
        }
        return defaultValue;
    }

    @Override
    public V put(String key, V value) {
        String oldKey = this._k.put(convertKey(key), key);
        if (oldKey != null && !oldKey.equals(key)) {
            this._m.remove(oldKey);
        }
        return this._m.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }
        map.forEach(this::put);
    }

    @Override
    public V remove(Object key) {
        if (key instanceof String) {
            String key2 = this._k.remove(convertKey((String) key));
            if (key2 != null) {
                return this._m.remove(key2);
            }
        }
        return null;
    }

    @Override
    public void clear() {
        this._k.clear();
        this._m.clear();
    }

    @Override
    public Set<String> keySet() {
        return this._m.keySet();
    }

    @Override
    public Collection<V> values() {
        return this._m.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return this._m.entrySet();
    }

    @Override
    public LinkedCaseInsensitiveMap<V> clone() {
        return new LinkedCaseInsensitiveMap<>(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this._m.equals(obj);
    }

    @Override
    public int hashCode() {
        return this._m.hashCode();
    }

    @Override
    public String toString() {
        return this._m.toString();
    }


    // Specific to LinkedCaseInsensitiveMap


    public Locale getLocale() {
        return this.locale;
    }

    protected String convertKey(String key) {
        return key.toLowerCase(getLocale());
    }

    protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        return false;
    }
}