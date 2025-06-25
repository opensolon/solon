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

import org.noear.solon.core.handle.Context;

/**
 * 版本解析器，基于 header 处理
 *
 * @author noear
 * @since 3.4
 */
public class VersionResolverHeader implements VersionResolver {
    private final String headerName;

    public VersionResolverHeader() {
        this("Api-Version");
    }

    public VersionResolverHeader(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public void versionResolve(Context ctx) {
        if (ctx.getVersion() == null) {
            ctx.setVersion(ctx.header(headerName));
        }
    }
}