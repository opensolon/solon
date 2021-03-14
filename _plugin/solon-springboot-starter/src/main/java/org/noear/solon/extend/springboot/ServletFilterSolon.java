package org.noear.solon.extend.springboot;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.extend.servlet.SolonServletContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author noear
 * @since 1.2
 * */
public class ServletFilterSolon implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //兼容 servlet 3.1.0
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        SolonServletContext ctx = new SolonServletContext((HttpServletRequest) request, (HttpServletResponse) response);
        //ctx.contentType("text/plain;charset=UTF-8");

        Solon.global().tryHandle(ctx);

        if (ctx.getHandled() == false) {
            ContextUtil.currentSet(ctx);
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        //兼容 servlet 3.1.0
    }
}
