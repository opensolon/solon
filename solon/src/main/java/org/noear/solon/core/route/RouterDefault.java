/*
 * Copyright 2017-2024 noear.org and authors
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

import org.noear.solon.Solon;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Constants;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.PathMatcher;

import java.util.*;

/**
 * 通用路由器默认实现
 *
 * @author noear
 * @since 1.3
 * @since 3.0
 */
public class RouterDefault implements Router, HandlerSlots {
    /**
     * 路由表
     *
     * @since 1.3
     * @since 3.0
     */
    private final RoutingTable<Handler> table = new RoutingTableDefault<>();

    @Override
    public void caseSensitive(boolean caseSensitive) {
        PathMatcher.setCaseSensitive(caseSensitive);
    }

    /**
     * 添加路由关系 for Handler
     *
     * @param path    路径
     * @param method  方法
     * @param index   顺序位
     * @param handler 处理接口
     */
    @Override
    public void add(String path, MethodType method, int index, Handler handler) {
        RoutingDefault routing = new RoutingDefault<>(path, method, index, handler);

        table.add(routing);
    }

    @Override
    public void add(BeanWrap controllerWrap) {
        if (controllerWrap != null) {
            ActionLoader al = Solon.app().factoryManager().mvcFactory()
                    .createLoader(controllerWrap);

            if (controllerWrap.remoting()) {
                if (al.mapping() == null) {
                    //如果类没有 mapping，则不进行  remoting注册
                    return;
                }
            }

            al.load(this);
        }
    }

    @Override
    public void add(String path, BeanWrap controllerWrap) {
        if (controllerWrap != null) {
            Solon.app().factoryManager().mvcFactory()
                    .createLoader(controllerWrap, path, controllerWrap.remoting(), null, true)
                    .load(this);
        }
    }

    @Override
    public Result<Handler> matchMainAndStatus(Context ctx) {
        //不能从缓存里取，不然 pathNew 会有问题
        String pathNew = ctx.pathNew();
        MethodType method = MethodTypeUtil.valueOf(ctx.method());

        Result<Handler> result = table.matchOneAndStatus(pathNew, method);

        if (result.getData() != null) {
            ctx.attrSet(Constants.ATTR_MAIN_HANDLER, result.getData());
            ctx.attrSet(Constants.ATTR_MAIN_STATUS, 200);
        } else {
            //要补这个 set null（多次时，可以不断重置）
            ctx.attrSet(Constants.ATTR_MAIN_HANDLER, null);
            ctx.attrSet(Constants.ATTR_MAIN_STATUS, result.getCode());
        }

        return result;
    }

    @Override
    public Handler matchMain(Context ctx) {
        //不能从缓存里取，不然 pathNew 会有问题
        String pathNew = ctx.pathNew();
        MethodType method = MethodTypeUtil.valueOf(ctx.method());


        return table.matchOne(pathNew, method);
    }


    /**
     * 获取某个处理点的所有路由记录（管理用）
     *
     * @return 处理点的所有路由记录
     */
    @Override
    public Collection<Routing<Handler>> getAll() {
        return table.getAll();
    }

    /**
     * 获取某个路径的某个处理点的路由记录（管理用）
     *
     * @param path 路径
     * @return 路径处理点的路由记录
     * @since 2.6
     */
    @Override
    public Collection<Routing<Handler>> getBy(String path) {
        return table.getBy(path);
    }

    @Override
    public Collection<Routing<Handler>> getBy(Class<?> controllerClz) {
        return table.getBy(controllerClz);
    }


    /**
     * 移除路由关系
     *
     * @param pathPrefix 路径前缀
     */
    @Override
    public void remove(String pathPrefix) {
        table.remove(pathPrefix);
    }

    @Override
    public void remove(Class<?> controllerClz) {
        table.remove(controllerClz);
    }

    /**
     * 清空路由关系
     */
    @Override
    public void clear() {
        table.clear();
    }

    //
    // HandlerSlots 接口实现
    //

    @Override
    public void add(String expr, MethodType method, Handler handler) {
        add(expr, method, 0, handler);
    }
}