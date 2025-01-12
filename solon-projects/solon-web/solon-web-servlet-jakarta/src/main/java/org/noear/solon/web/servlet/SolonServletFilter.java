/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.servlet;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextHolder;
import org.noear.solon.core.handle.Handler;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Servlet Filter，适配为 Solon 入口
 *
 * @author noear
 * @since 1.2
 * */
public class SolonServletFilter implements Filter {
    static final Logger log = LoggerFactory.getLogger(SolonServletFilter.class);

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
                ContextHolder.currentSet(ctx);

                //过滤开始
                doFilterStart(ctx);

                //Solon处理(可能是空处理)
                Solon.app().tryHandle(ctx);

                //重新设置当前上下文（上面会清掉）
                ContextHolder.currentSet(ctx);

                if (ctx.getHandled() == false) {
                    //如果未处理，则传递过滤链
                    filterChain.doFilter(request, response);
                }
            } catch (Throwable e) {
                ctx.errors = e;
                doFilterError(ctx);

                throw e;
            } finally {
                //过滤结束
                doFilterEnd(ctx);
                ContextHolder.currentRemove();
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
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    @Override
    public void destroy() {
        //兼容 servlet 3.1.0
    }
}
