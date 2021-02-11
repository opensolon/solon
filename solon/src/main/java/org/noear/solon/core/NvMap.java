package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.ext.LinkedCaseInsensitiveMap;

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

    public NvMap(Map<String, String> map) {
        super();
        putAll(map);
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
            int len = args.size();

            for (int i = 0; i < len; i++) {
                String arg = args.get(i).replaceAll("-*", "");

                if (arg.indexOf("=") > 0) {
                    String[] ss = arg.split("=");
                    d.put(ss[0], ss[1]);
                } else {
                    if (i + 1 < len) {
                        d.put(arg, args.get(i + 1));
                    }
                    i++;
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
}
