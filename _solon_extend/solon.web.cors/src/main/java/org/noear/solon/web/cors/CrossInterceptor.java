package org.noear.solon.web.cors;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;

/**
 * 跨域处理
 *
 * @author noear
 * @since 1.12
 */
public class CrossInterceptor implements RouterInterceptor {

    protected int maxAge = 3600;

    protected String allowedOrigins = "*";

    protected String allowedMethods = "*";
    protected String allowedHeaders = "*";
    protected boolean allowCredentials = true; //1.6起，默认为true

    protected String exposedHeaders;


    public CrossInterceptor maxAge(int maxAge) {
        if (maxAge >= 0) {
            this.maxAge = maxAge;
        }

        return this;
    }

    /**
     * 原点
     */
    public CrossInterceptor allowedOrigins(String allowOrigin) {
        if (allowOrigin != null) {
            this.allowedOrigins = allowOrigin;
        }

        return this;
    }

    public CrossInterceptor allowedMethods(String allowMethods) {
        this.allowedMethods = allowMethods;
        return this;
    }

    public CrossInterceptor allowedHeaders(String allowHeaders) {
        this.allowedHeaders = allowHeaders;
        return this;
    }

    public CrossInterceptor allowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
        return this;
    }

    public CrossInterceptor exposedHeaders(String exposeHeaders) {
        this.exposedHeaders = exposeHeaders;
        return this;
    }


    protected void doIntercept0(Context ctx) throws Throwable {
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

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        doIntercept0(ctx);

        if(ctx.getHandled() == false){
            chain.doIntercept(ctx, mainHandler);
        }
    }
}
