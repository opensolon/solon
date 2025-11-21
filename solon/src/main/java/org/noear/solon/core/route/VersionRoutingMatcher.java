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

import org.noear.solon.core.handle.MethodType;

import java.util.List;

/**
 * 版本路由匹配器（高性能专用）
 * 
 * @author noear
 * @since 3.4
 */
public class VersionRoutingMatcher<T> {
    
    /**
     * 快速匹配版本化路由
     * 
     * @param versionedTable 版本路由表（已按优先级排序）
     * @param path 请求路径
     * @param version 请求版本
     * @param method 请求方法
     * @return 匹配的目标，null表示未匹配
     */
    public static <T> T matchOne(List<org.noear.solon.core.util.RankEntity<Routing<T>>> versionedTable, 
                                String path, String version, MethodType method) {
        if (versionedTable == null || versionedTable.isEmpty()) {
            return null;
        }
        
        // 预编译请求版本（避免重复解析）
        Version requestVersion = Version.of(version);
        if (requestVersion == null) {
            return null;
        }
        
        // 遍历已排序的路由表，找到第一个匹配的
        for (org.noear.solon.core.util.RankEntity<Routing<T>> entity : versionedTable) {
            Routing<T> routing = entity.target;
            
            // 快速路径：先检查方法匹配（通常比版本检查更快）
            if (!methodMatches(routing.method(), method)) {
                continue;
            }
            
            // 版本匹配：使用预编译的版本对象
            Version routeVersion = ((RoutingDefault<T>)routing).getCachedVersion();
            if (routeVersion != null && routeVersion.matches(version)) {
                return routing.target();
            }
        }
        
        return null;
    }
    
    /**
     * 批量匹配版本化路由
     * 
     * @param versionedTable 版本路由表（已按优先级排序）
     * @param path 请求路径
     * @param version 请求版本
     * @param method 请求方法
     * @return 匹配的目标列表
     */
    public static <T> List<T> matchMore(List<org.noear.solon.core.util.RankEntity<Routing<T>>> versionedTable, 
                                      String path, String version, MethodType method) {
        java.util.List<T> results = new java.util.ArrayList<>();
        
        if (versionedTable == null || versionedTable.isEmpty()) {
            return results;
        }
        
        // 预编译请求版本
        Version requestVersion = Version.of(version);
        if (requestVersion == null) {
            return results;
        }
        
        // 收集所有匹配的路由
        for (org.noear.solon.core.util.RankEntity<Routing<T>> entity : versionedTable) {
            Routing<T> routing = entity.target;
            
            if (!methodMatches(routing.method(), method)) {
                continue;
            }
            
            Version routeVersion = ((RoutingDefault<T>)routing).getCachedVersion();
            if (routeVersion != null && routeVersion.matches(version)) {
                results.add(routing.target());
            }
        }
        
        return results;
    }
    
    /**
     * 快速方法匹配
     */
    private static boolean methodMatches(MethodType routeMethod, MethodType requestMethod) {
        if (MethodType.ALL == routeMethod) {
            return true;
        } else if (MethodType.HTTP == routeMethod) {
            return requestMethod.signal == org.noear.solon.core.SignalType.HTTP;
        } else {
            return requestMethod == routeMethod;
        }
    }
    
    /**
     * 计算匹配度（用于状态判断）
     * 
     * @param versionedTable 版本路由表
     * @param path 请求路径
     * @param version 请求版本
     * @param method 请求方法
     * @return 匹配状态结果
     */
    public static <T> org.noear.solon.core.handle.Result<T> matchOneAndStatus(
            List<org.noear.solon.core.util.RankEntity<Routing<T>>> versionedTable, 
            String path, String version, MethodType method) {
        
        if (versionedTable == null || versionedTable.isEmpty()) {
            return org.noear.solon.core.handle.Result.failure(404);
        }
        
        Version requestVersion = Version.of(version);
        if (requestVersion == null) {
            return org.noear.solon.core.handle.Result.failure(404);
        }
        
        int maxDegree = 0;
        T bestMatch = null;
        
        for (org.noear.solon.core.util.RankEntity<Routing<T>> entity : versionedTable) {
            Routing<T> routing = entity.target;
            
            int degree = routing.degrees(method, path, version);
            if (degree == 2) {
                // 完全匹配，直接返回
                return org.noear.solon.core.handle.Result.succeed(routing.target());
            } else if (degree > maxDegree) {
                maxDegree = degree;
                bestMatch = routing.target();
            }
        }
        
        if (maxDegree == 1) {
            return org.noear.solon.core.handle.Result.failure(405);
        } else if (bestMatch != null) {
            return org.noear.solon.core.handle.Result.succeed(bestMatch);
        } else {
            return org.noear.solon.core.handle.Result.failure(404);
        }
    }
}