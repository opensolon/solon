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
package org.noear.solon.cloud.gateway.exchange;

import org.noear.solon.core.util.RunUtil;
import org.noear.solon.rx.Completable;

/**
 * 交换过滤器同步形态（用于对接同步 io）
 *
 * @author noear
 * @since  3.1
 */
@FunctionalInterface
public interface ExFilterSync extends ExFilter {
    @Override
    default Completable doFilter(ExContext ctx, ExFilterChain chain) {
        return Completable.create(emitter -> {
            //暂停接收流
            ctx.pause();

            //开始异步
            RunUtil.async(() -> {
                try {
                    //开始同步处理
                    boolean isContinue = doFilterSync(ctx);

                    if (isContinue) {
                        //继续
                        chain.doFilter(ctx).subscribe(emitter);
                    } else {
                        //结束
                        emitter.onComplete();
                    }
                } catch (Throwable ex) {
                    emitter.onError(ex);
                }
            });
        });
    }

    /**
     * 执行过滤同步处理（一般用于同步 io）
     *
     * @param ctx 上下文
     * @return 是否继续
     */
    boolean doFilterSync(ExContext ctx) throws Throwable;
}