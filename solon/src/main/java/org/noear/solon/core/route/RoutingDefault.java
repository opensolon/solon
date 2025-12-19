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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 路由默认实现
 *
 * @author noear
 * @since 1.3
 * @since 3.4
 */
public class RoutingDefault<T> implements Routing<T> {
    private static final Logger log = LoggerFactory.getLogger(RoutingDefault.class);
    public RoutingDefault(String path, MethodType method, int index) {
        if (path.startsWith("/") == false) {
            path = "/" + path;
        }

        this.rule = PathMatcher.get(path, false);

        this.method = method;
        this.path = path;
        this.globstar = path.indexOf("/**");
        this.matchall = path.equals("/**");


        this.index = index;
    }

    private final PathMatcher rule; //path rule 规则

    private final int index; //顺序
    private final String path; //path
    private final MethodType method; //方式
    private final int globstar;
    private final boolean matchall;

    private VersionedTarget<T> versionedTargetNull;//目标
    private TreeMap<Version,VersionedTarget<T>> versionedTargets = new TreeMap<>();

    public RoutingDefault<T> addVersionTarget(Version version, T target) {
        // 检测重复
        if(version == null){
            version = Version.EMPTY;
        }

        // 添加目标
        VersionedTarget tmp = new VersionedTarget<>(version, target);

        if (version.isEmpty()) {
            if (versionedTargetNull != null) {
                //可以被替换，但要有日志提示
                log.warn("The routing replaced: '{}'", path);
            }

            versionedTargetNull = tmp;
        } else {
            if (versionedTargets.containsKey(version)) {
                //可以被替换，但要有日志提示
                log.warn("The routing version({}) replaced: '{}'", version.getOriginal(), path);
            }
        }

        versionedTargets.put(version, tmp);

        return this;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public int globstar() {
        return globstar;
    }

    /**
     * 获取所有目标
     *
     * @since 3.7
     */
    @Override
    public Collection<VersionedTarget<T>> targets() {
        return versionedTargets.values();
    }

    /**
     * 匹配版本目标
     *
     * @since 3.7
     */
    @Override
    public T target(Version version2) {
        if (version2 == null) {
            if (versionedTargetNull != null) {
                return versionedTargetNull.getTarget();
            } else {
                version2 = Version.EMPTY;
            }
        }

        VersionedTarget<T> exact = versionedTargets.get(version2);
        if (exact != null) {
            return exact.getTarget();
        }

        for(Map.Entry<Version, VersionedTarget<T>> entry : versionedTargets.entrySet()) {
            if (entry.getKey().includes(version2)) {
                return entry.getValue().getTarget();
            }
        }

        return null;
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
     */
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
        //1.如果当前为 /**，任何路径都可命中
        if (matchall) {
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