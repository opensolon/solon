package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.handler.Context;
import org.noear.solon.ext.LinkedCaseInsensitiveMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 可排序，不区分大小写
 *
 * 用于：参数解析，Header，Param 处理
 *
 * @see Context#paramMap()
 * @author noear
 * @since 1.0
 * */
public class ParamMap extends LinkedCaseInsensitiveMap<String> {

    public ParamMap() {
        super();
    }

    public ParamMap(Map<String, String> map) {
        super();
        putAll(map);
    }

    public static ParamMap from(String[] args) {
        return from(Arrays.asList(args));
    }

    public static ParamMap from(List<String> args) {
        ParamMap d = new ParamMap();

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
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return 0;
        } else {
            return Integer.parseInt(temp);
        }
    }

    public long getLong(String key) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return 0l;
        } else {
            return Long.parseLong(temp);
        }
    }

    public double getDouble(String key) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return 0d;
        } else {
            return Double.parseDouble(temp);
        }
    }
}
