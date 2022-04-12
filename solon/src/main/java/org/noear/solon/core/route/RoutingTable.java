package org.noear.solon.core.route;

import org.noear.solon.core.handle.MethodType;

import java.util.List;

/**
 * 路由表
 *
 * @author noear
 * @since 1.7
 */
public interface RoutingTable<T> {
    /**
     * 添加路由记录
     *
     * @param routing 路由
     */
    void add(Routing<T> routing);

    /**
     * 添加路由记录
     *
     * @param routing 路由
     * @param index 索引位置
     */
    void add(int index, Routing<T> routing);

    /**
     * 获取所有路由记录
     * */
    List<Routing<T>> getAll();

    /**
     * 区配一个目标
     *
     * @param path   路径
     * @param method 方法
     * @return 一个区配的目标
     */
    T matchOne(String path, MethodType method);

    /**
     * 区配多个目标
     *
     * @param path   路径
     * @param method 方法
     * @return 一批区配的目标
     */
    List<T> matchAll(String path, MethodType method);

    /**
     * 清空
     * */
    void clear();
}
