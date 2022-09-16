package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.LinkedCaseInsensitiveMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 可排序，不区分大小写（Name value map）
 *
 * 用于：参数解析，Header，Param 处理
 *
 * @see Context#paramMap()
 * @author noear
 * @since 1.0
 * */
public class NvMap extends LinkedCaseInsensitiveMap<String> {

    public NvMap() {
        super();
    }

    public NvMap(Map map) {
        super();

        if (map != null) {
            map.forEach((k, v) -> {
                if (k != null && v != null) {
                    put(k.toString(), v.toString());
                }
            });
        }
    }

    public NvMap set(String key, String val) {
        put(key, val);
        return this;
    }

    public static NvMap from(String[] args) {
        return from(Arrays.asList(args));
    }

    public static NvMap from(List<String> args) {
        NvMap d = new NvMap();

        if (args != null) {
            for (String arg : args) {
                int index = arg.indexOf('=');
                if (index > 0) {
                    String name = arg.substring(0, index);
                    String value = arg.substring(index + 1);
                    d.put(name.replaceAll("^-*", ""), value);
                } else {
                    d.put(arg.replaceAll("^-*", ""), "");
                }
            }
        }

        return d;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int def) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return Integer.parseInt(temp);
        }
    }

    public long getLong(String key) {
        return getLong(key, 0l);
    }

    public long getLong(String key, long def) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return Long.parseLong(temp);
        }
    }

    public double getDouble(String key) {
        return getDouble(key, 0d);
    }

    public double getDouble(String key, double def) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return Double.parseDouble(temp);
        }
    }

    public boolean getBool(String key, boolean def) {
        if (containsKey(key)) {
            return Boolean.parseBoolean(get(key));
        } else {
            return def;
        }
    }

    public <T> T getBean(Class<T> clz) {
        return PropsConverter.global().convert(new Props(this), clz);
    }
}
