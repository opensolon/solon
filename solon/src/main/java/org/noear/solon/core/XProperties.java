package org.noear.solon.core;

import org.noear.solon.XUtil;
import org.noear.solon.ext.Act2;
import org.noear.solon.ext.Fun1;

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

    public boolean getBool(String key, boolean def){
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

    private <T> T getOrDef(String key, T def, Fun1<String,T> convert){
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return convert.run(temp);
        }
    }

    public Object getRaw(String key){
        return super.get(key);
    }

    public XProperties getProp(String key) {
        XProperties prop = new XProperties();
        find(key, prop::put);
        return prop;
    }

    public XMap getXmap(String key) {
        XMap map = new XMap();
        find(key,map::put);
        return map;
    }

    private void find(String key, Act2<String,String> setFun){
        String key2 = key + ".";
        int idx2 = key2.length();

        String keyStr = null;
        for (Map.Entry<Object, Object> kv : this.entrySet()) {
            keyStr = kv.getKey().toString();
            if (keyStr.startsWith(key2)) {
                setFun.run(keyStr.substring(idx2), kv.getValue().toString());
            }
        }
    }

    public void bindTo(Object target) {
        XUtil.bindTo((k) -> get(k), target);
    }
}
