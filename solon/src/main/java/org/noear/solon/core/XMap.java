package org.noear.solon.core;

import org.noear.solon.XUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/*
* 应用参数（转换启动命令参数）
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
