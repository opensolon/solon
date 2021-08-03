package org.noear.solon.extend.cors;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 跨域处理
 *
 * @author noear
 * @since 1.3
 */
public class CrossHandler implements Handler {
    protected int maxAge = 3600;

    protected String allowOrigin = "*";

    protected String allowMethods;
    protected String allowHeaders;
    protected boolean allowCredentials;


    public CrossHandler maxAge(int maxAge) {
        if (maxAge >= 0) {
            this.maxAge = maxAge;
        }

        return this;
    }

    public CrossHandler allowOrigin(String allowOrigin) {
        if (allowOrigin != null) {
            this.allowOrigin = allowOrigin;
        }

        return this;
    }

    public CrossHandler allowMethods(String allowMethods) {
        this.allowMethods = allowMethods;
        return this;
    }

    public CrossHandler allowHeaders(String allowHeaders) {
        this.allowHeaders = allowHeaders;
        return this;
    }

    public CrossHandler allowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
        return this;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        ctx.headerSet("Access-Control-Allow-Origin", allowOrigin);
        ctx.headerSet("Access-Control-Max-Age", String.valueOf(maxAge));

        if (Utils.isNotEmpty(allowHeaders)) {
            ctx.headerSet("Access-Control-Allow-Headers", allowHeaders);
        }

        if (Utils.isNotEmpty(allowMethods)) {
            ctx.headerSet("Access-Control-Allow-Methods", allowMethods);
        }

        if (allowCredentials) {
            ctx.headerSet("Access-Control-Allow-Credentials", String.valueOf(allowCredentials));
        }
    }
}
