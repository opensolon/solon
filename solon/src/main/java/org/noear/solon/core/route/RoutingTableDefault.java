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
    private LinkedList<RankEntity<Routing<T>>> table = new LinkedList<>();


    /**
     * 添加路由记录
     *
     * @param routing 路由
     */
    @Override
    public void add(Routing<T> routing) {
        int level = 0;

        if (routing.path().indexOf('{') >= 0) {
            level = 1;
        }

        if (routing.path().indexOf('*') >= 0) {
            level = 2;
        }

        // 计算版本优先级：精确匹配版本 > 模式匹配版本 > 无版本
        int versionLevel = getVersionPriority(routing);
//        RankEntity<Routing<T>> entity = new RankEntity<>(routing, level, routing.index(), false);
        RankEntity<Routing<T>> entity = new RankEntity<>(routing, routing.index(), versionLevel, true);

        if (level != 0 || routing.index() != 0 || versionLevel != 0) {
            //有 * 号的 或有 index 的 或有版本优先级的；排序下
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
        String version = routing.version();
        if (version == null || version.isEmpty()) {
            return 0; // 无版本优先级最低
        }
        
        if (version.endsWith("+")) {
            // 模式匹配：将版本号转换为数值用于比较
            String baseVersion = version.substring(0, version.length() - 1);
            return calculateVersionValue(baseVersion);
        } else {
            // 精确匹配：比模式匹配优先级更高，在相同版本基础上加分
            return calculateVersionValue(version) + 10000; // 加分确保精确匹配优先
        }
    }
    
    /**
     * 计算版本号的数值（用于排序）
     * 例如：1.2.3 -> 1*1000000 + 2*1000 + 3 = 1002003
     * 
     * @param version 版本号
     * @return 版本数值
     */
    private int calculateVersionValue(String version) {
        if (version == null || version.isEmpty()) {
            return 0;
        }
        
        String[] parts = version.split("\\.");
        int value = 0;
        int multiplier = 1000000; // 从最高位开始
        
        for (String part : parts) {
            try {
                // 移除非数字字符
                String numericPart = part.replaceAll("[^0-9]", "");
                int partValue = numericPart.isEmpty() ? 0 : Integer.parseInt(numericPart);
                value += partValue * multiplier;
                multiplier /= 1000; // 每个部分的权重递减
            } catch (NumberFormatException e) {
                // 解析失败则使用 0
            }
        }
        
        return value;
    }

    /**
     * 移除路由记录
     *
     * @param pathPrefix 路径前缀
     */
    @Override
    public void remove(String pathPrefix) {
        table.removeIf(l -> l.target.path().startsWith(pathPrefix));
    }

    /**
     * 移除路由记录
     *
     * @param controllerClz 控制器类
     */
    @Override
    public void remove(Class<?> controllerClz) {
        table.removeIf(l -> {
            if (l.target.target() instanceof Action) {
                Action a = (Action) l.target.target();
                if (a.controller().clz().equals(controllerClz)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public int count() {
        return table.size();
    }

    @Override
    public Collection<Routing<T>> getAll() {
        return table.stream().map(l -> l.target).collect(Collectors.toList());
    }

    @Override
    public Collection<Routing<T>> getBy(String pathPrefix) {
        return table.stream()
                .filter(l -> l.target.path().startsWith(pathPrefix))
                .map(l -> l.target)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Routing<T>> getBy(Class<?> controllerClz) {
        return table.stream()
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
                .collect(Collectors.toList());
    }

    /**
     * 区配一个目标
     *
     * @param path   路径
     * @param method 方法
     * @return 一个区配的目标
     */
    public T matchOne(String path, String version, MethodType method) {
        // 路由表已按优先级排序，直接返回第一个匹配的即可
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
        int degrees = 0;
        
        // 路由表已按优先级排序，直接返回第一个匹配的即可
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
        return table.stream()
                .filter(l -> l.target.matches(method, path, version))
                .map(l -> l.target.target())
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        table.clear();
    }
}