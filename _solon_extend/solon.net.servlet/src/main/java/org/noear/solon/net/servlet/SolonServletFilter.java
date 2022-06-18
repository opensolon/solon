package org.noear.solon.net.servlet;

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
            Context ctx = new SolonServletContext((HttpServletRequest) request, (HttpServletResponse) response);

            try {
                ContextUtil.currentSet(ctx);

                //过滤开始
                doFilterStart(ctx);

                //Solon处理(可能是空处理)
                Solon.app().tryHandle(ctx);

                //重新设置当前上下文（上面会清掉）
                ContextUtil.currentSet(ctx);

                if (ctx.getHandled() == false) {
                    //如果未处理，则传递过滤链
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
        doHandler(onFilterStart, ctx);
    }

    protected void doFilterError(Context ctx) {
        doHandler(onFilterError, ctx);
    }

    protected void doFilterEnd(Context ctx) {
        doHandler(onFilterEnd, ctx);
    }

    protected void doHandler(Handler h, Context ctx){
        if (h != null) {
            try {
                h.handle(ctx);
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
