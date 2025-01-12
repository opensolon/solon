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

import org.noear.solon.core.SignalType;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PathMatcher;

/**
 * 路由默认实现
 *
 * @author noear
 * @since 1.3
 */
public class RoutingDefault<T> implements Routing<T> {

    public RoutingDefault(String path, MethodType method, T target) {
        this(path, method, 0, target);
    }

    public RoutingDefault(String path, MethodType method, int index, T target) {
        this.rule = PathMatcher.get(path);

        this.method = method;
        this.path = path;
        this.index = index;
        this.target = target;
    }

    private final PathMatcher rule; //path rule 规则

    private final int index; //顺序
    private final String path; //path
    private final T target;//代理
    private final MethodType method; //方式

    @Override
    public int index() {
        return index;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public T target() {
        return target;
    }

    @Override
    public MethodType method() {
        return method;
    }

    /**
     * 是否匹配
     */
    @Override
    public boolean matches(MethodType method2, String path2) {
        if (MethodType.ALL == method) {
            return matches0(path2);
        } else if (MethodType.HTTP == method) { //不是null时，不能用==
            if (method2.signal == SignalType.HTTP) {
                return matches0(path2);
            }
        } else if (method2 == method) {
            return matches0(path2);
        }

        return false;
    }

    /**
     * 匹配程度
     *
     * @since 2.5
     * */
    @Override
    public int degrees(MethodType method2, String path2) {
        if (matches0(path2)) {
            if (MethodType.ALL == method) {
                return 2;
            } else if (MethodType.HTTP == method) { //不是null时，不能用==
                if (method2.signal == SignalType.HTTP) {
                    return 2;
                }
            } else if (method2 == method) {
                return 2;
            }

            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean test(String path2) {
        return matches0(path2);
    }

    private boolean matches0(String path2) {
        //1.如果当前为**，任何路径都可命中
        if ("**".equals(path) || "/**".equals(path)) {
            return true;
        }

        //2.如果与当前路径相关
        if (path.equals(path2)) {
            return true;
        }

        //3.正则检测
        return rule.matches(path2);
    }

    @Override
    public String toString() {
        return "Routing{" +
                "index=" + index +
                ", method=" + method +
                ", path='" + path + '\'' +
                '}';
    }
}