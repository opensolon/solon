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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<VersionedTarget<T>> versionedTargets = new ArrayList<>();
    private Set<String> versionSet; //用于快速检测重复版本

    public RoutingDefault<T> addVersionTarget(Version version, T target) {
        // 检测重复
        if (version == null) {
            if (versionedTargetNull != null) {
                log.error("The routing repeated: '{}'", path);
                //return this;
            }
        } else {
            if(versionSet == null){
                //懒加载，减少内存点用
                versionSet = new HashSet<>();
            }

            String versionKey = version.getOriginal();
            if (!versionSet.add(versionKey)) {
                log.error("The routing version({}) repeated: '{}'", versionKey, path);
                //return this;
            }
        }

        // 添加目标
        VersionedTarget tmp = new VersionedTarget<>(version, target);

        if (tmp.getVersion() == null) {
            versionedTargetNull = tmp;
        }

        versionedTargets.add(tmp);

        if (versionedTargets.size() > 1) {
            Collections.sort(versionedTargets);
        }

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
    public List<VersionedTarget<T>> targets() {
        return versionedTargets;
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
            }
        }

        for (VersionedTarget<T> tmp : versionedTargets) {
            if (tmp.getVersion() != null && tmp.getVersion().includes(version2)) {
                return tmp.getTarget();
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