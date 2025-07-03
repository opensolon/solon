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
package org.noear.solon.web.version;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 版本过滤器
 *
 * @author noear
 * @since 3.4
 */
public class VersionFilter implements Filter {
    private final List<VersionResolver> resolverList = new ArrayList<>();

    /**
     * 使用头
     */
    public VersionFilter useHeader(String headerName) {
        this.resolverList.add((ctx) -> ctx.header(headerName));
        return this;
    }

    /**
     * 使用参数
     */
    public VersionFilter useParam(String paramName) {
        this.resolverList.add((ctx) -> ctx.param(paramName));
        return this;
    }

    /**
     * 使用路径段（从0开始）
     */
    public VersionFilter usePathSegment(int index) {
        this.resolverList.add(new PathVersionResolver(index));
        return this;
    }

    /**
     * 使用定制版本分析器
     */
    public VersionFilter useVersionResolver(VersionResolver... resolvers) {
        this.resolverList.addAll(Arrays.asList(resolvers));
        return this;
    }

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        for (VersionResolver resolver : resolverList) {
            if (Utils.isEmpty(ctx.getVersion())) {
                ctx.setVersion(resolver.versionResolve(ctx));
            } else {
                break;
            }
        }

        chain.doFilter(ctx);
    }
}