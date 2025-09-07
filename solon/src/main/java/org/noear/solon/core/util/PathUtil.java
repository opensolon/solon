/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.util;

import org.noear.solon.Solon;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 路径工具
 *
 * @author noear
 * @since 1.2
 * */
public class PathUtil {
    /**
     * 合并地址和路径
     */
    public static String joinUri(String server, String path) {
        if (server.endsWith("/")) {
            if (path.startsWith("/")) {
                return server + path.substring(1);
            } else {
                return server + path;
            }
        } else {
            if (path.startsWith("/")) {
                return server + path;
            } else {
                return server + "/" + path;
            }
        }
    }

    /**
     * 合并两个路径
     */
    public static String mergePath(String path1, String path2) {
        if (Assert.isEmpty(path1) || "**".equals(path1) || "/**".equals(path1)) {
            if (path2.startsWith("/")) {
                return path2;
            } else {
                return "/" + path2;
            }
        }

        if (path1.startsWith("/") == false) {
            path1 = "/" + path1;
        }

        if (Assert.isEmpty(path2)) {
            if (path1.endsWith("*")) {
                //支持多个*情况
                int idx = path1.lastIndexOf('/') + 1;
                if (idx < 1) {
                    return "/";
                } else {
                    return path1.substring(0, idx) + path2;
                }
            } else {
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

    public static final Pattern pathKeyExpr = Pattern.compile("\\{([^\\\\}]+)\\}");

    /**
     * 将路径根据表达式转成map
     */
    public static Map<String, String> pathVarMap(String path, String expr) {
        IgnoreCaseMap<String> _map = new IgnoreCaseMap<>();

        //支持path变量
        if (expr.indexOf("{") >= 0) {
            String path2 = null;
            try {
                path2 = URLDecoder.decode(path, Solon.encoding());
            } catch (Throwable ex) {
                path2 = path;
            }

            Matcher pm = pathKeyExpr.matcher(expr);

            List<String> _pks = new ArrayList<>();

            while (pm.find()) {
                _pks.add(pm.group(1));
            }

            if (_pks.size() > 0) {
                PathMatcher _pr = PathMatcher.get(expr);

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
