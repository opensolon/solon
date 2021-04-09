package org.noear.solon.extend.servlet;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.core.handle.Handler;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author noear
 * @since 1.2
 * */
public class SolonServletFilter implements Filter {
    public static Handler onFilterStart;
    public static Handler onFilterEnd;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //兼容 servlet 3.1.0
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            Context ctx = new SolonServletContext((HttpServletRequest) request, (HttpServletResponse) response);

            //过滤开始
            doFilterStart(ctx);

            Solon.global().tryHandle(ctx);

            if (ctx.getHandled() == false) {
                ContextUtil.currentSet(ctx);
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    ContextUtil.currentRemove();
                }
            }

            //过滤结束
            doFilterEnd(ctx);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public void doFilterStart(Context ctx) {
        if (onFilterStart != null) {
            try {
                onFilterStart.handle(ctx);
            } catch (Throwable ex) {
                EventBus.push(ex);
            }
        }
    }

    public void doFilterEnd(Context ctx) {
        if (onFilterEnd != null) {
            try {
                onFilterEnd.handle(ctx);
            } catch (Throwable ex) {
                EventBus.push(ex);
            }
        }
    }

    @Override
    public void destroy() {
        //兼容 servlet 3.1.0
    }
}
