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

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.RankEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 路由表默认实现
 *
 * @author noear
 * @since 1.0
 * @since 3.4
 * */
public class RoutingTableDefault<T> implements RoutingTable<T> {
    /**
     * 普通路由表（无版本）
     */
    private LinkedList<RankEntity<Routing<T>>> table = new LinkedList<>();
    
    /**
     * 版本路由表（按版本号排序）
     */
    private Map<String, LinkedList<RankEntity<Routing<T>>>> versionedTables = new HashMap<>();


    /**
     * 添加路由记录
     *
     * @param routing 路由
     */
    @Override
    public void add(Routing<T> routing) {
        String version = routing.version();
        
        if (version != null && !version.isEmpty()) {
            // 有版本的路由：按路径+版本分组
            addVersionedRouting(routing);
        } else {
            // 普通路由：使用原有逻辑
            addNormalRouting(routing);
        }
    }
    
    /**
     * 添加版本化路由
     */
    private void addVersionedRouting(Routing<T> routing) {
        String path = routing.path();
        
        // 获取或创建该路径的版本路由表
        LinkedList<RankEntity<Routing<T>>> versionTable = versionedTables.computeIfAbsent(path, k -> new LinkedList<>());
        
        // 计算版本优先级
        int versionLevel = getVersionPriority(routing);
        
        RankEntity<Routing<T>> entity = new RankEntity<>(routing, routing.index(), versionLevel, true);
        versionTable.add(entity);
        
        // 按版本优先级排序（从高到低）
        Collections.sort(versionTable);
    }
    
    /**
     * 添加普通路由
     */
    private void addNormalRouting(Routing<T> routing) {
        String path = routing.path();
        int level = 0;
        
        if (path.indexOf('{') >= 0) {
            level = 1;
        }
        if (path.indexOf('*') >= 0) {
            level = 2;
        }
        
        RankEntity<Routing<T>> entity = new RankEntity<>(routing,level ,routing.index(), false);
        
        if (level != 0 || routing.index() != 0) {
            //有 * 号的 或有 index 的；排序下
            table.addLast(entity);
            Collections.sort(table);
        } else {
            table.addFirst(entity);
        }
    }
    
    /**
     * 计算版本优先级
     * 返回值越大，优先级越高
     * 
     * @param routing 路由
     * @return 版本优先级值
     */
    public int getVersionPriority(Routing<T> routing) {
        String versionStr = routing.version();
        Version version = Version.of(versionStr);
        
        if (version == null) {
            return 0;
        }
        
        // 计算版本数值：major * 1000000 + minor * 1000 + patch
        int versionValue = version.getMajor() * 1000000 + 
                         version.getMinor() * 1000 + 
                         version.getPatch();
        
        if (version.isPattern()) {
            // 模式匹配：基础值
            return versionValue;
        } else {
            // 精确匹配：加分确保精确匹配优先
            return versionValue + 10000;
        }
    }

    /**
     * 移除路由记录
     *
     * @param pathPrefix 路径前缀
     */
    @Override
    public void remove(String pathPrefix) {
        // 从普通路由表中移除
        table.removeIf(l -> l.target.path().startsWith(pathPrefix));
        
        // 从版本路由表中移除
        versionedTables.entrySet().removeIf(entry -> entry.getKey().startsWith(pathPrefix));
    }

    /**
     * 移除路由记录
     *
     * @param controllerClz 控制器类
     */
    @Override
    public void remove(Class<?> controllerClz) {
        // 从普通路由表中移除
        table.removeIf(l -> {
            if (l.target.target() instanceof Action) {
                Action a = (Action) l.target.target();
                if (a.controller().clz().equals(controllerClz)) {
                    return true;
                }
            }
            return false;
        });
        
        // 从版本路由表中移除
        versionedTables.values().forEach(versionTable -> {
            versionTable.removeIf(l -> {
                if (l.target.target() instanceof Action) {
                    Action a = (Action) l.target.target();
                    if (a.controller().clz().equals(controllerClz)) {
                        return true;
                    }
                }
                return false;
            });
        });
    }

    @Override
    public int count() {
        int count = table.size();
        for (LinkedList<RankEntity<Routing<T>>> versionTable : versionedTables.values()) {
            count += versionTable.size();
        }
        return count;
    }

    @Override
    public Collection<Routing<T>> getAll() {
        List<Routing<T>> allRoutes = new ArrayList<>();

        // 添加普通路由
        allRoutes.addAll(table.stream().map(l -> l.target).collect(Collectors.toList()));
        
        // 添加版本路由
        for (LinkedList<RankEntity<Routing<T>>> versionTable : versionedTables.values()) {
            allRoutes.addAll(versionTable.stream().map(l -> l.target).collect(Collectors.toList()));
        }
        
        return allRoutes;
    }

    @Override
    public Collection<Routing<T>> getBy(String pathPrefix) {
        List<Routing<T>> matchedRoutes = new ArrayList<>();
        
        // 从普通路由表中查找
        matchedRoutes.addAll(table.stream()
                .filter(l -> l.target.path().startsWith(pathPrefix))
                .map(l -> l.target)
                .collect(Collectors.toList()));
        
        // 从版本路由表中查找
        for (Map.Entry<String, LinkedList<RankEntity<Routing<T>>>> entry : versionedTables.entrySet()) {
            if (entry.getKey().startsWith(pathPrefix)) {
                matchedRoutes.addAll(entry.getValue().stream()
                        .map(l -> l.target)
                        .collect(Collectors.toList()));
            }
        }
        
        return matchedRoutes;
    }

    @Override
    public Collection<Routing<T>> getBy(Class<?> controllerClz) {
        List<Routing<T>> matchedRoutes = new ArrayList<>();
        
        // 从普通路由表中查找
        matchedRoutes.addAll(table.stream()
                .filter(l -> {
                    if (l.target.target() instanceof Action) {
                        Action a = (Action) l.target.target();
                        if (a.controller().clz().equals(controllerClz)) {
                            return true;
                        }
                    }
                    return false;
                })
                .map(l -> l.target)
                .collect(Collectors.toList()));
        
        // 从版本路由表中查找
        for (LinkedList<RankEntity<Routing<T>>> entry : versionedTables.values()) {
            matchedRoutes.addAll(entry.stream().filter(l -> {
                if (l.target.target() instanceof Action) {
                    Action a = (Action) l.target.target();
                    if (a.controller().clz().equals(controllerClz)) {
                        return true;
                    }
                }
                return false;
            }).map(l -> l.target).collect(Collectors.toList()));
        }
        
        return matchedRoutes;
    }

    /**
     * 区配一个目标
     *
     * @param path   路径
     * @param method 方法
     * @return 一个区配的目标
     */
    public T matchOne(String path, String version, MethodType method) {
        // 优先匹配版本化路由
        if (version != null && !version.isEmpty()) {
            LinkedList<RankEntity<Routing<T>>> versionTable = versionedTables.get(path);
            T result = VersionRoutingMatcher.matchOne(versionTable, path, version, method);
            if (result != null) {
                return result;
            }
        }
        
        // 回退到普通路由表
        for (RankEntity<Routing<T>> l : table) {
            if (l.target.matches(method, path, version)) {
                return l.target.target();
            }
        }

        return null;
    }

    /**
     * 区配一个目标并给出状态
     *
     * @param path   路径
     * @param method 方法
     * @return 一个区配的目标
     */
    @Override
    public Result<T> matchOneAndStatus(String path, String version, MethodType method) {
        // 优先匹配版本化路由
        if (version != null && !version.isEmpty()) {
            LinkedList<RankEntity<Routing<T>>> versionTable = versionedTables.get(path);
            Result<T> result = VersionRoutingMatcher.matchOneAndStatus(versionTable, path, version, method);
            if (result.getData() != null || result.getCode() != 404) {
                return result;
            }
        }
        
        // 回退到普通路由表
        int degrees = 0;
        for (RankEntity<Routing<T>> l : table) {
            int tmp = l.target.degrees(method, path, version);
            if (tmp == 2) {
                return Result.succeed(l.target.target());
            } else {
                if (tmp > degrees) {
                    degrees = tmp;
                }
            }
        }

        if (degrees == 1) {
            return Result.failure(405);
        } else {
            return Result.failure(404);
        }
    }

    /**
     * 区配多个目标
     *
     * @param path   路径
     * @param method 方法
     * @return 一批区配的目标
     */
    @Override
    public List<T> matchMore(String path, String version, MethodType method) {
        List<T> matchedTargets = new ArrayList<>();
        
        // 优先从版本路由表中查找
        if (version != null && !version.isEmpty()) {
            LinkedList<RankEntity<Routing<T>>> versionTable = versionedTables.get(path);
            matchedTargets.addAll(VersionRoutingMatcher.matchMore(versionTable, path, version, method));
        }
        
        // 从普通路由表中查找
        matchedTargets.addAll(table.stream()
                .filter(l -> l.target.matches(method, path, version))
                .map(l -> l.target.target())
                .collect(Collectors.toList()));
        
        return matchedTargets;
    }

    @Override
    public void clear() {
        table.clear();
        versionedTables.clear();
    }
}