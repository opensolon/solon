package org.noear.solon.extend.servlet;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.ContextUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author noear 2020/12/28 created
 */
public class SolonServletFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        SolonServletContext ctx = new SolonServletContext((HttpServletRequest) request, (HttpServletResponse) response);
        ctx.contentType("text/plain;charset=UTF-8");


        try {
            Solon.global().handle(ctx);

            if (ctx.getHandled() == false) {
                ContextUtil.currentSet(ctx);
                filterChain.doFilter(request, response);
            }

        } catch (Throwable ex) {
            EventBus.push(ex);
            ctx.statusSet(500);

            if (Solon.cfg().isDebugMode()) {
                ex.printStackTrace(response.getWriter());
            }
        } finally {
            ContextUtil.currentRemove();
        }
    }
}
