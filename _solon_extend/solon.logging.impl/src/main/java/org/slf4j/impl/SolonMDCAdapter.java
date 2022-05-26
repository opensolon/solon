package org.slf4j.impl;

import org.slf4j.spi.MDCAdapter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/2/26 created
 */
public class SolonMDCAdapter implements MDCAdapter {

    private static final ThreadLocal<Map<String, String>> threadMap = new InheritableThreadLocal<>();

    @Override
    public void put(String key, String val) {
        Map<String, String> ht = threadMap.get();
        if (ht == null) {
            ht = new LinkedHashMap<>();
            threadMap.set(ht);
        }

        ht.put(key, val);
    }

    @Override
    public String get(String key) {
        Map<String, String> ht = threadMap.get();
        if (ht != null) {
            return ht.get(key);
        } else {
            return null;
        }
    }

    @Override
    public void remove(String key) {
        Map<String, String> ht = threadMap.get();
        if (ht != null) {
            ht.remove(key);
        }
    }

    @Override
    public void clear() {
        threadMap.set(null);
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> map = threadMap.get();
        if (map != null) {
            return new LinkedHashMap<>(map);
        } else {
            return map;
        }
    }

    @Override
    public void setContextMap(Map<String, String> map) {
        threadMap.set(map);
    }
}
