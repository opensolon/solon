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
    public static Handler onFilterError;
    public static Handler onFilterEnd;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //兼容 servlet 3.1.0
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            //
            // 临时对接，parseMultipart=false 免得对数据有影响
            //
            Context ctx = new SolonServletContext((HttpServletRequest) request, (HttpServletResponse) response, false);

            try {
                ContextUtil.currentSet(ctx);

                //过滤开始
                doFilterStart(ctx);

                Solon.global().tryHandle(ctx);

                ContextUtil.currentSet(ctx);

                if (ctx.getHandled() == false) {
                    filterChain.doFilter(request, response);
                }
            } catch (Throwable err) {
                ctx.errors = err;
                doFilterError(ctx);

                throw err;
            } finally {
                //过滤结束
                doFilterEnd(ctx);
                ContextUtil.currentRemove();
            }

        } else {
            filterChain.doFilter(request, response);
        }
    }

    protected void doFilterStart(Context ctx) {
        if (onFilterStart != null) {
            try {
                onFilterStart.handle(ctx);
            } catch (Throwable ex) {
                EventBus.push(ex);
            }
        }
    }

    protected void doFilterError(Context ctx) {
        if (onFilterError != null) {
            try {
                onFilterError.handle(ctx);
            } catch (Throwable ex) {
                EventBus.push(ex);
            }
        }
    }

    protected void doFilterEnd(Context ctx) {
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
