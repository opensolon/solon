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
package org.noear.solon.web.cors;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

/**
 * @author noear
 * @since 1.12
 */
public abstract class AbstractCross<T extends AbstractCross> {
    protected int maxAge = 3600;

    protected String allowedOrigins = "*";

    protected String allowedMethods = "*";
    protected String allowedHeaders = "*";
    protected boolean allowCredentials = true; //1.6起，默认为true

    protected String exposedHeaders;


    public T maxAge(int maxAge) {
        if (maxAge >= 0) {
            this.maxAge = maxAge;
        }

        return (T)this;
    }

    /**
     * 原点
     */
    public T allowedOrigins(String allowOrigin) {
        if (allowOrigin != null) {
            this.allowedOrigins = allowOrigin;
        }

        return (T)this;
    }

    public T allowedMethods(String allowMethods) {
        this.allowedMethods = allowMethods;
        return (T)this;
    }

    public T allowedHeaders(String allowHeaders) {
        this.allowedHeaders = allowHeaders;
        return (T)this;
    }

    public T allowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
        return (T)this;
    }

    public T exposedHeaders(String exposeHeaders) {
        this.exposedHeaders = exposeHeaders;
        return (T)this;
    }

    protected void doHandle(Context ctx) throws Throwable {
        if(ctx.getHandled()){
            return;
        }

        String origin = ctx.header("Origin");

        if (Utils.isEmpty(origin)) {
            //如果没有 Origin 则不输出 Cross Header
            return;
        }

        //设定 max age
        ctx.headerSet("Access-Control-Max-Age", String.valueOf(maxAge));

        //设定 allow headers
        if (Utils.isNotEmpty(allowedHeaders)) {
            if ("*".equals(allowedHeaders)) {
                String requestHeaders = ctx.header("Access-Control-Request-Headers");

                if (Utils.isNotEmpty(requestHeaders)) {
                    ctx.headerSet("Access-Control-Allow-Headers", requestHeaders);
                }
            } else {
                ctx.headerSet("Access-Control-Allow-Headers", allowedHeaders);
            }
        }

        //设定 allow methods
        if (Utils.isNotEmpty(allowedMethods)) {
            if ("*".equals(allowedMethods)) {
                String requestMethod = ctx.header("Access-Control-Request-Method");

                //如果没有请求方式，则使用当前请求方式
                if (Utils.isEmpty(requestMethod)) {
                    requestMethod = ctx.method();
                }

                if (Utils.isNotEmpty(requestMethod)) {
                    ctx.headerSet("Access-Control-Allow-Methods", requestMethod);
                }
            } else {
                ctx.headerSet("Access-Control-Allow-Methods", allowedMethods);
            }
        }

        //设定 allow origin
        if (Utils.isNotEmpty(allowedOrigins)) {
            if ("*".equals(allowedOrigins) || allowedOrigins.contains(origin)) {
                ctx.headerSet("Access-Control-Allow-Origin", origin);
            }
        }


        if (allowCredentials) {
            ctx.headerSet("Access-Control-Allow-Credentials", "true");
        }


        if (Utils.isNotEmpty(exposedHeaders)) {
            ctx.headerSet("Access-Control-Expose-Headers", exposedHeaders);
        }

        if (MethodType.OPTIONS.name.equalsIgnoreCase(ctx.method())) {
            ctx.setHandled(true);
        }
    }
}
