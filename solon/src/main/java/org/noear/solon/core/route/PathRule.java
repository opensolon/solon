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
package org.noear.solon.core.route;

import org.noear.solon.core.util.PathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 路径规则
 *
 * @author noear
 * @since 2.3
 */
public class PathRule implements Predicate<String> {
    /**
     * 拦截规则（包函规则）
     */
    private List<PathMatcher> includeList = new ArrayList<>();
    /**
     * 放行规则（排除规则）
     */
    private List<PathMatcher> excludeList = new ArrayList<>();

    public PathRule include(String... patterns) {
        for (String p1 : patterns) {
            includeList.add(PathMatcher.get(p1));
        }

        return this;
    }

    public PathRule exclude(String... patterns) {
        for (String p1 : patterns) {
            excludeList.add(PathMatcher.get(p1));
        }

        return this;
    }

    /**
     * 是否为空
     * */
    public boolean isEmpty(){
        return includeList.isEmpty() && excludeList.isEmpty();
    }

    /**
     * 规则检测
     * */
    @Override
    public boolean test(String path) {
        //1.放行匹配
        for (PathMatcher pa : excludeList) {
            if (pa.matches(path)) {
                return false;
            }
        }

        //2.拦截匹配
        for (PathMatcher pa : includeList) {
            if (pa.matches(path)) {
                return true;
            }
        }

        //3.无匹配，//如果有排除，又没排除掉；且没有包函；则需要处理
        return (excludeList.size() > 0 && includeList.size() == 0);
    }
}
