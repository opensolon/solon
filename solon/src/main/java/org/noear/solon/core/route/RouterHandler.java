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

import org.noear.solon.core.ChainManager;
import org.noear.solon.core.Constants;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.*;

/**
 * 路由处理器
 *
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public class RouterHandler implements Handler {
    private Router router;
    private ChainManager chainManager;

    public RouterHandler(Router router, ChainManager chainManager) {
        this.router = router;
        this.chainManager = chainManager;
    }

    /**
     * 主处理
     */
    protected boolean handleMain(Handler h, Context ctx) throws Throwable {
        if (h != null) {
            /**
             * 从原 bef("**", MethodType.HTTP) 迁过来
             *
             * @since 3.0
             * */
            chainManager.refreshSessionState(ctx);

            h.handle(ctx);
            return ctx.status() != 404;
        } else {
            int code = ctx.attrOrDefault(Constants.ATTR_MAIN_STATUS, 404);
            if (code == 405) {
                throw new StatusException("Method Not Allowed: " + ctx.method() + " " + ctx.pathNew(), code);
            } else {
                throw new StatusException("Not Found: " + ctx.method() + " " + ctx.pathNew(), code);
            }
            //return false;
        }
    }

    private void handle1(Context x) throws Throwable {
        try {
            //主体处理
            if (x.getHandled() == false) { //保留这个，过滤器可以有两种控制方式（软控，硬控）
                //（仅支持唯一代理）
                //（设定处理状态，便于 after 获取状态）
                Handler mainHandler = x.mainHandler();
                x.setHandled(handleMain(mainHandler, x));
            }
        } catch (Throwable e) {
            if (x.errors == null) {
                x.errors = e; //如果内部已经做了，就不需要了
            }
            throw e;
        }
    }

    @Override
    public void handle(Context x) throws Throwable {
        //可能上级链已完成处理
        if (x.getHandled()) {
            return;
        }

        //提前获取主处理
        Result<Handler> mainHandler = router.matchMainAndStatus(x);

        //执行
        chainManager.doIntercept(x, mainHandler.getData(), this::handle1);
    }
}