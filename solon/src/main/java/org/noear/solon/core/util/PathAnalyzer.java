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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 路径分析器，用于路由器和路径变量分析
 *
 * @author noear
 * @since 1.0
 * @deprecated 3.0 {@link PathMatcher}
 * */
@Deprecated
public class PathAnalyzer {
    /**
     * 分析器缓存
     */
    private static final Map<String, PathAnalyzer> cached = new ConcurrentHashMap<>();

    public static void setCaseSensitive(boolean caseSensitive) {
        PathMatcher.setCaseSensitive(caseSensitive);
    }

    public static PathAnalyzer get(String expr) {
        return get(expr, true);
    }

    public static PathAnalyzer get(String expr, boolean addStarts) {
        if (addStarts) {
            if (expr.startsWith("/") == false) {
                expr = "/" + expr;
            }
        }

        return cached.computeIfAbsent(expr, k -> new PathAnalyzer(k, PathMatcher.isCaseSensitive()));
    }


    private Pattern pattern;
    private Pattern patternNoStart;


    private PathAnalyzer(String expr, boolean caseSensitive) {
        if (caseSensitive) {
            pattern = Pattern.compile(PathMatcher.exprCompile(expr, true));

            if (expr.contains("{")) {
                patternNoStart = Pattern.compile(PathMatcher.exprCompile(expr, false));
            }
        } else {
            pattern = Pattern.compile(PathMatcher.exprCompile(expr, true), Pattern.CASE_INSENSITIVE);

            if (expr.contains("{")) {
                patternNoStart = Pattern.compile(PathMatcher.exprCompile(expr, false), Pattern.CASE_INSENSITIVE);
            }
        }
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

}