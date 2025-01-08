/*
 * Copyright 2017-2024 noear.org and authors
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

import org.noear.solon.core.handle.Action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 路径匹配器，用于路由器和路径变量分析
 *
 * @see org.noear.solon.core.route.RoutingDefault
 * @see Action
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public class PathMatcher {
    private static boolean _caseSensitive = true;
    /**
     * 分析器缓存
     */
    private static final Map<String, PathMatcher> _cached = new ConcurrentHashMap<>();

    /**
     * 设置区分大小写
     */
    public static void setCaseSensitive(boolean caseSensitive) {
        PathMatcher._caseSensitive = caseSensitive;
    }

    /**
     * 是否区分大小写（默认区分）
     */
    public static boolean isCaseSensitive() {
        return _caseSensitive;
    }


    /**
     * 获取路径匹配器
     */
    public static PathMatcher get(String expr) {
        return get(expr, true);
    }

    /**
     * 获取路径匹配器
     */
    public static PathMatcher get(String expr, boolean addStarts) {
        if (addStarts) {
            if (expr.startsWith("/") == false) {
                expr = "/" + expr;
            }
        }

        return _cached.computeIfAbsent(expr, k -> new PathMatcher(k, _caseSensitive));
    }


    private Pattern pattern;
    private Pattern patternNoStart;
    private int depth;

    public PathMatcher(String expr, boolean caseSensitive) {
        if (caseSensitive) {
            pattern = Pattern.compile(exprCompile(expr, true));

            if (expr.contains("{")) {
                patternNoStart = Pattern.compile(exprCompile(expr, false));
            }
        } else {
            pattern = Pattern.compile(exprCompile(expr, true), Pattern.CASE_INSENSITIVE);

            if (expr.contains("{")) {
                patternNoStart = Pattern.compile(exprCompile(expr, false), Pattern.CASE_INSENSITIVE);
            }
        }

        String[] exprFragments = expr.split("/");
        for (int i = 0; i < exprFragments.length; ++i) {
            if (depthIndexOf(exprFragments[i]) < 0) {
                depth++;
            } else {
                break;
            }
        }
    }

    private static int depthIndexOf(String str) {
        for (int j = 0; j < str.length(); ++j) {
            char c = str.charAt(j);
            if (c == '{' || c == '*') {
                return j;
            }
        }

        return -1;
    }

    /**
     * 获取路径常量深度
     */
    public int depth() {
        return depth;
    }

    /**
     * 路径匹配变量
     */
    public Matcher matcher(String uri) {
        if (patternNoStart != null) {
            return patternNoStart.matcher(uri);
        } else {
            return pattern.matcher(uri);
        }
    }

    /**
     * 检测是否匹配
     */
    public boolean matches(String uri) {
        return pattern.matcher(uri).find();
    }

    /**
     * 将路径表达式编译为正则表达式
     *
     * @param expr       表达式
     * @param fixedStart 固定起始符
     */
    protected static String exprCompile(String expr, boolean fixedStart) {
        //替换特殊符号
        String p = expr;

        p = p.replace(".", "\\.");
        p = p.replace("$", "\\$");

        //替换中间的**值
        p = p.replace("**", ".[]");

        //替换*值
        p = p.replace("*", "[^/]*");

        //替换{x}值
        if (p.indexOf("{") >= 0) {
            if (p.indexOf("_}") > 0) {
                p = p.replaceAll("\\{[^\\}]+?\\_\\}", "(.+)");
            }
            p = p.replaceAll("\\{[^\\}]+?\\}", "([^/]+)");//不采用group name,可解决_的问题
        }

        p = p.replace(".[]", ".*");

        //整合并输出
        if (fixedStart) {
            return "^" + p + "$";
        } else {
            return p + "$";
        }
    }
}