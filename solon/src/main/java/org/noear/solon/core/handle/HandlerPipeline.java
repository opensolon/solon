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
package org.noear.solon.core.handle;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理管道，提供处理链的存储
 *
 * @author noear
 * @since 1.0
 */
public class HandlerPipeline implements Handler {
    private List<Handler> chain = new ArrayList<>();

    /**
     * 下一步
     * */
    public HandlerPipeline next(Handler handler) {
        chain.add(handler);
        return this;
    }

    /**
     * 上一步
     * */
    public HandlerPipeline prev(Handler handler) {
        chain.add(0, handler);
        return this;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        for (Handler h : chain) {
            if (ctx.getHandled()) {
                break;
            }

            h.handle(ctx);
        }
    }
}
