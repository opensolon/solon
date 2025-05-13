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

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * 提供 ContextPath 类似的功能（优先级要极高）
 *
 * @author noear
 * @since 1.8
 */
public class ContextPathFilter implements Filter {
    private final String contextPath0;
    private final String contextPath1;
    private final boolean forced;

    public ContextPathFilter() {
        this(Solon.cfg().serverContextPath(), Solon.cfg().serverContextPathForced());
    }

    /**
     * @param contextPath '/demo/'
     */
    public ContextPathFilter(String contextPath, boolean forced) {
        this.forced = forced;

        if (Utils.isEmpty(contextPath)) {
            contextPath0 = null;
            contextPath1 = null;
        } else {
            String newPath = null;
            if (contextPath.endsWith("/")) {
                newPath = contextPath;
            } else {
                newPath = contextPath + "/";
            }

            if (newPath.startsWith("/")) {
                this.contextPath1 = newPath;
            } else {
                this.contextPath1 = "/" + newPath;
            }

            this.contextPath0 = contextPath1.substring(0, contextPath1.length() - 1);

            //有可能是 ContextPathFilter 是用户手动添加的！需要补一下配置
            Solon.cfg().serverContextPath(this.contextPath1);
        }
    }


    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (contextPath0 != null) {
            if (ctx.pathNew().equals(contextPath0)) {
                //www:888 加 abc 后，仍可以用 www:888/abc 打开
                ctx.pathNew("/");
            } else if (ctx.pathNew().startsWith(contextPath1)) {
                ctx.pathNew(ctx.pathNew().substring(contextPath1.length() - 1));
            } else {
                if (forced) {
                    ctx.status(404);
                    return;
                }
            }
        }

        chain.doFilter(ctx);
    }
}