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

        RankEntity<Routing<T>> entity = new RankEntity<>(routing, level, routing.index(), false);

        if (level != 0 || routing.index() != 0) {
            //有 * 号的 或有 index 的；排序下
            table.addLast(entity);
            Collections.sort(table);
        } else {
            table.addFirst(entity);
        }
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
    public Collection<Routing<T>> getBy(String path) {
        return table.stream()
                .filter(l -> l.target.test(path))
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
    public T matchOne(String path, MethodType method) {
        for (RankEntity<Routing<T>> l : table) {
            if (l.target.matches(method, path)) {
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
    public Result<T> matchOneAndStatus(String path, MethodType method) {
        int degrees = 0;
        for (RankEntity<Routing<T>> l : table) {
            int tmp = l.target.degrees(method, path);
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
    public List<T> matchMore(String path, MethodType method) {
        return table.stream()
                .filter(l -> l.target.matches(method, path))
                .map(l -> l.target.target())
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        table.clear();
    }
}