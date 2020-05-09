package org.noear.solon.core;

import org.noear.solon.XUtil;
import org.noear.solon.ext.Act2;
import org.noear.solon.ext.Fun1;

import java.util.Properties;
import java.util.function.BiConsumer;

public class XProperties extends Properties {

    public XProperties() {
        super();
    }

    public XProperties(Properties defaults) {
        super(defaults);
    }

    /**
     * 获取某项配置
     */
    public String get(String key) {
        return getProperty(key);
    }

    public String get(String key, String def) {
        return getProperty(key, def);
    }

    public boolean getBool(String key, boolean def) {
        return getOrDef(key, def, Boolean::parseBoolean);
    }

    public int getInt(String key, int def) {
        return getOrDef(key, def, Integer::parseInt);
    }

    public long getLong(String key, long def) {
        return getOrDef(key, def, Long::parseLong);
    }

    public Double getDouble(String key, double def) {
        return getOrDef(key, def, Double::parseDouble);
    }

    private <T> T getOrDef(String key, T def, Fun1<String, T> convert) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return convert.run(temp);
        }
    }

    public Object getRaw(String key) {
        return super.get(key);
    }

    public XProperties getProp(String key) {
        XProperties prop = new XProperties();
        find(key, prop::put);
        return prop;
    }

    public XMap getXmap(String key) {
        XMap map = new XMap();
        find(key, map::put);
        return map;
    }

    private void find(String key, Act2<String, String> setFun) {
        String key2 = key + ".";
        int idx2 = key2.length();

        forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                String keyStr = (String) k;

                if (keyStr.startsWith(key2)) {
                    setFun.run(keyStr.substring(idx2), (String)v);
                }
            }
        });
    }

    @Override
    public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
        if (defaults != null) {
            defaults.forEach(action);
        }

        super.forEach(action);
    }
}
