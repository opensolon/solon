package org.noear.solon.core.handle;

import org.noear.solon.Solon;

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

    /**
     * @param contextPath '/demo/'
     */
    public ContextPathFilter(String contextPath, boolean forced) {
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
        this.forced = forced;

        //有可能是 ContextPathFilter 是用户手动添加的！需要补一下配置
        Solon.cfg().serverContextPath(this.contextPath1);
    }

    public ContextPathFilter(String contextPath) {
        this(contextPath, false);
    }


    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (ctx.pathNew().equals(contextPath0)) {
            //www:888 加 abc 后，仍可以用 www:888/abc 打开
            ctx.pathNew("/");
        } else if (ctx.pathNew().startsWith(contextPath1)) {
            ctx.pathNew(ctx.pathNew().substring(contextPath1.length() - 1));
        } else {
            if (forced) {
                return;
            }
        }

        chain.doFilter(ctx);
    }
}
