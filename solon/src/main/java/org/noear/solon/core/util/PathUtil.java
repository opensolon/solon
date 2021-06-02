package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtil {
    /**
     * 合并两个路径
     */
    public static String mergePath(String path1, String path2) {
        if (Utils.isEmpty(path1) || "**".equals(path1) || "/**".equals(path1)) {
            if (path2.startsWith("/")) {
                return path2;
            } else {
                return "/" + path2;
            }
        }

        if (path1.startsWith("/") == false) {
            path1 = "/" + path1;
        }

        if (Utils.isEmpty(path2)) {
            if (path1.endsWith("*")) {
                //支持多个*情况
                int idx = path1.lastIndexOf('/') + 1;
                if (idx < 1) {
                    return "/";
                } else {
                    return path1.substring(0, idx) + path2;
                }
            }else {
                return path1;
            }
        }

        if (path2.startsWith("/")) {
            path2 = path2.substring(1);
        }

        if (path1.endsWith("/")) {
            return path1 + path2;
        } else {
            if (path1.endsWith("*")) {
                //支持多个*情况
                int idx = path1.lastIndexOf('/') + 1;
                if (idx < 1) {
                    return path2;
                } else {
                    return path1.substring(0, idx) + path2;
                }
            } else {
                return path1 + "/" + path2;
            }
        }
    }

    private static Pattern _pkr = Pattern.compile("\\{([^\\\\}]+)\\}");

    /**
     * 将路径根据表达式转成map
     * */
    public static NvMap pathVarMap(String path, String expr) {
        NvMap _map = new NvMap();

        //支持path变量
        if (expr.indexOf("{") >= 0) {
            String path2 = null;
            try {
                path2 = URLDecoder.decode(path, "utf-8");
            } catch (Throwable ex) {
                path2 = path;
            }

            Matcher pm = _pkr.matcher(expr);

            List<String> _pks = new ArrayList<>();

            while (pm.find()) {
                _pks.add(pm.group(1));
            }

            if (_pks.size() > 0) {
                PathAnalyzer _pr = PathAnalyzer.get(expr);

                pm = _pr.matcher(path2);
                if (pm.find()) {
                    for (int i = 0, len = _pks.size(); i < len; i++) {
                        _map.put(_pks.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                    }
                }
            }
        }

        return _map;
    }
}
