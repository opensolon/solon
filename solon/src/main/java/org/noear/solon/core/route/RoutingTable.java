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
import org.noear.solon.core.handle.Result;

import java.util.Collection;
import java.util.List;

/**
 * 路由表
 *
 * @author noear
 * @since 1.7
 * @since 3.4
 */
public interface RoutingTable<T> {
    /**
     * 添加路由记录
     *
     * @param routing 路由
     */
    void add(Routing<T> routing);

    /**
     * 移除路由记录
     *
     * @param pathPrefix 路径前缀
     */
    void remove(String pathPrefix);

    /**
     * 移除路由记录
     *
     * @param controllerClz 控制器类
     */
    void remove(Class<?> controllerClz);

    /**
     * 数量
     */
    int count();

    /**
     * 清空
     */
    void clear();

    /**
     * 获取所有的路由记录
     */
    Collection<Routing<T>> getAll();

    /**
     * 获取路径的路由记录
     *
     * @param path 路径
     * @since 2.6
     */
    Collection<Routing<T>> getBy(String path);

    /**
     * 获取控制器的路由记录
     *
     * @param controllerClz 控制器类
     * @since 2.8
     */
    Collection<Routing<T>> getBy(Class<?> controllerClz);

    /**
     * 区配一个目标
     *
     * @param path    路径
     * @param version 版本号
     * @param method  方法
     * @return 一个区配的目标
     * @since 1.0
     * @since 3.4
     */
    T matchOne(String path, String version, MethodType method);

    /**
     * 区配一个目标并给出状态
     *
     * @param path    路径
     * @param version 版本号
     * @param method  方法
     * @return 一个区配的目标
     * @since 2.5
     * @since 3.4
     */
    Result<T> matchOneAndStatus(String path, String version, MethodType method);


    /**
     * 区配多个目标
     *
     * @param path    路径
     * @param version 版本号
     * @param method  方法
     * @return 一批区配的目标
     * @since 2.5
     * @since 3.4
     */
    List<T> matchMore(String path, String version, MethodType method);


    /**
     * 区配一个目标
     *
     * @since 1.0
     * @deprecated 3.4
     */
    @Deprecated
    default T matchOne(String path, MethodType method) {
        return matchOne(path, null, method);
    }

    /**
     * 区配一个目标并给出状态
     *
     * @since 2.5
     * @deprecated 3.4
     */
    @Deprecated
    default Result<T> matchOneAndStatus(String path, MethodType method) {
        return matchOneAndStatus(path, null, method);
    }

    /**
     * 区配多个目标
     *
     * @since 2.5
     * @deprecated 3.4
     */
    @Deprecated
    default List<T> matchMore(String path, MethodType method) {
        return matchMore(path, null, method);
    }
}