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
package org.noear.solon.flow;

import org.noear.solon.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步任务组件
 *
 * @author noear
 * @since 3.1
 */
public abstract class AsyncTaskComponent implements TaskComponent {
    static Logger log = LoggerFactory.getLogger(AsyncTaskComponent.class);

    @Override
    public void run(ChainContext context, Node node) throws Throwable {
        //阻断
        context.interrupt();

        Utils.async(() -> {
            try {
                //异步执行
                asyncRun(context, node);

                //跳到下节点
                context.engine().next(node, context);
            } catch (Throwable ex) {
                //如错时
                onError(context, node, ex);
            }
        });
    }

    /**
     * 出错时
     */
    protected void onError(ChainContext context, Node node, Throwable ex) {
        log.warn(ex.getMessage(), ex);
        context.stop();
    }

    /**
     * 异步运行
     */
    protected abstract void asyncRun(ChainContext context, Node node) throws Throwable;
}
