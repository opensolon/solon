package org.noear.solon.extend.servlet;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author noear
 * @since 1.2
 * */
public class SolonServletFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //兼容 servlet 3.1.0
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            Context ctx = new SolonServletContext((HttpServletRequest) request, (HttpServletResponse) response);
            Solon.global().tryHandle(ctx);

            if (ctx.getHandled() == false) {
                ContextUtil.currentSet(ctx);
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    ContextUtil.currentRemove();
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        //兼容 servlet 3.1.0
    }
}
