package org.noear.solon.core;

import org.noear.solon.XUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
* 可排序，不区分大小写
*
* 用于：参数解析，Header，Param 处理
* */
public class XMap extends LinkedHashMap<String, String> {

    public XMap(){
        super();
    }

    public XMap(Map<String,String> map){
        super(map);
    }

    public static XMap from(String[] args) {
        XMap d = new XMap();

        if (args != null) {
            int len = args.length;

            for (int i = 0; i < len; i++) {
                String arg = args[i].replaceAll("-*", "");

                if (arg.indexOf("=") > 0) {
                    String[] ss = arg.split("=");
                    d.put(ss[0], ss[1]);
                } else {
                    if (i + 1 < len) {
                        d.put(arg, args[i + 1]);
                    }
                    i++;
                }
            }
        }

        return d;
    }

    public static XMap from(List<String> args) {
        XMap d = new XMap();

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

    /**
     * 不区分大小写
     * */
    @Override
    public String get(Object key) {
        if (key == null) {
            return null;
        }

        for (Map.Entry<String, String> kv : this.entrySet()) {
            if (kv.getKey() != null) {
                if (kv.getKey().equalsIgnoreCase(key.toString())) {
                    return kv.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 不区分大小写
     * */
    @Override
    public String getOrDefault(Object key, String defaultValue) {
        if(key == null){
            return defaultValue;
        }

        for (Map.Entry<String, String> kv : this.entrySet()) {
            if (kv.getKey() != null) {
                if (kv.getKey().equalsIgnoreCase(key.toString())) {
                    return kv.getValue();
                }
            }
        }

        return defaultValue;
    }

    public int getInt(String key) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return 0;
        } else {
            return Integer.parseInt(temp);
        }
    }

    public long getLong(String key) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return 0l;
        } else {
            return Long.parseLong(temp);
        }
    }

    public double getDouble(String key) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return 0d;
        } else {
            return Double.parseDouble(temp);
        }
    }
}
