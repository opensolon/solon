package org.noear.solon.extend.cors;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;

/**
 * 跨域处理
 *
 * @author noear
 * @since 1.3
 */
public class CrossHandler implements Handler {
    protected int maxAge = 3600;

    protected String allowedOrigins = "*";

    protected String allowedMethods = "*";
    protected String allowedHeaders = "*";
    protected boolean allowCredentials;

    protected String exposedHeaders;


    public CrossHandler maxAge(int maxAge) {
        if (maxAge >= 0) {
            this.maxAge = maxAge;
        }

        return this;
    }

    /**
     * 原点
     */
    public CrossHandler allowedOrigins(String allowOrigin) {
        if (allowOrigin != null) {
            this.allowedOrigins = allowOrigin;
        }

        return this;
    }

    public CrossHandler allowedMethods(String allowMethods) {
        this.allowedMethods = allowMethods;
        return this;
    }

    public CrossHandler allowedHeaders(String allowHeaders) {
        this.allowedHeaders = allowHeaders;
        return this;
    }

    public CrossHandler allowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
        return this;
    }

    public CrossHandler exposedHeaders(String exposeHeaders) {
        this.exposedHeaders = exposeHeaders;
        return this;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
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
        if (allowCredentials) {
            if ("*".equals(allowedOrigins) || allowedOrigins.contains(origin)) {
                ctx.headerSet("Access-Control-Allow-Credentials", "true");
                ctx.headerSet("Access-Control-Allow-Origin", origin);
            }
        } else {
            ctx.headerSet("Access-Control-Allow-Origin", allowedOrigins);
        }

        if (Utils.isNotEmpty(exposedHeaders)) {
            ctx.headerSet("Access-Control-Expose-Headers", exposedHeaders);
        }

        if (MethodType.OPTIONS.name.equalsIgnoreCase(ctx.method())) {
            ctx.setHandled(true);
        }
    }
}
