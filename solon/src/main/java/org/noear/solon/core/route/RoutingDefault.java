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

import org.noear.solon.Utils;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PathMatcher;

/**
 * 路由默认实现
 *
 * @author noear
 * @since 1.3
 * @since 3.4
 */
public class RoutingDefault<T> implements Routing<T> {

    public RoutingDefault(String path, String version, MethodType method, T target) {
        this(path, version, method, 0, target);
    }

    public RoutingDefault(String path, String version, MethodType method, int index, T target) {
        this.rule = PathMatcher.get(path);

        this.method = method;
        this.path = path;

        if (Utils.isEmpty(version)) {
            this.version = null;
        } else {
            this.version = version;
        }

        this.index = index;
        this.target = target;
    }

    private final PathMatcher rule; //path rule 规则

    private final int index; //顺序
    private final String path; //path
    private final String version;
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
    public String version() {
        return version;
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
     * 版本是否匹配（支持模式匹配）
     * 
     * @param routeVersion 路由定义的版本（如 "1.0", "1.0+"）
     * @param requestVersion 请求的版本（如 "1.0", "1.1", "1.2"）
     * @return 是否匹配
     */
    private boolean matchesVersion(String routeVersion, String requestVersion) {
        if (Utils.isEmpty(routeVersion) || Utils.isEmpty(requestVersion)) {
            return false;
        }
        
        // 精确匹配
        if (routeVersion.equals(requestVersion)) {
            return true;
        }
        
        // 模式匹配：支持 "1.0+" 格式
        if (routeVersion.endsWith("+")) {
            String baseVersion = routeVersion.substring(0, routeVersion.length() - 1);
            return compareVersions(requestVersion, baseVersion) >= 0;
        }
        
        // 不匹配
        return false;
    }
    
    /**
     * 比较两个版本号
     * 
     * @param version1 版本1
     * @param version2 版本2
     * @return 返回值：<0 表示 version1 < version2；==0 表示 version1 == version2；>0 表示 version1 > version2
     */
    private int compareVersions(String version1, String version2) {
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");
        
        int maxLength = Math.max(v1Parts.length, v2Parts.length);
        
        for (int i = 0; i < maxLength; i++) {
            int v1Part = i < v1Parts.length ? parseVersionPart(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? parseVersionPart(v2Parts[i]) : 0;
            
            int result = Integer.compare(v1Part, v2Part);
            if (result != 0) {
                return result;
            }
        }
        
        return 0;
    }
    
    /**
     * 解析版本号的部分
     * 
     * @param versionPart 版本号部分字符串
     * @return 解析后的整数
     */
    private int parseVersionPart(String versionPart) {
        try {
            // 移除可能存在的非数字字符（如预发布版本标识）
            String numericPart = versionPart.replaceAll("[^0-9]", "");
            if (numericPart.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    


    /**
     * 是否匹配
     */
    @Override
    public boolean matches(MethodType method2, String path2, String version2) {
        if (MethodType.ALL == method) {
            return matches0(path2, version2);
        } else if (MethodType.HTTP == method) { //不是null时，不能用==
            if (method2.signal == SignalType.HTTP) {
                return matches0(path2, version2);
            }
        } else if (method2 == method) {
            return matches0(path2, version2);
        }

        return false;
    }

    /**
     * 匹配程度
     *
     * @since 2.5
     */
    @Override
    public int degrees(MethodType method2, String path2, String version2) {
        if (matches0(path2, version2)) {
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
    public boolean test(String path2, String version2) {
        return matches0(path2, version2);
    }

    private boolean matches0(String path2, String version2) {
        if (Utils.isNotEmpty(version)) {
            //如果有版本申明
            if (!matchesVersion(version, version2)) {
                return false;
            }
        } else if (Utils.isNotEmpty(version2)) {
            //如果有版本要求
            return false;
        }

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

    private boolean matchesPath0(String path2) {
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