package com.fujieid.jap.ids.solon.http;

import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.ErrorEndpoint;
import com.fujieid.jap.ids.exception.IdsException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 * @author noear
 * @since 1.6
 */
public class ErrorFilter implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        try {
            chain.doFilter(ctx);
            // 判断此次请求是否为 HttpServletRequest 请求
            if(ctx.request() instanceof HttpServletRequest) {
                // 抛出验证异常
                HttpServletRequest request = (HttpServletRequest) ctx.request();

                if (request.getAttribute("javax.servlet.error.exception") != null) {
                    throw (Throwable) request.getAttribute("javax.servlet.error.exception");
                }
            }
        } catch (IdsException exception) {
            new ErrorEndpoint().showErrorPage(
                    exception.getError(),
                    exception.getErrorDescription(),
                    new JakartaResponseAdapter((HttpServletResponse) ctx.response())
            );
        }
    }
}
