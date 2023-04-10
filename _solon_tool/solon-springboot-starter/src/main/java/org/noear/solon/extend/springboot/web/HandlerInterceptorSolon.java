package org.noear.solon.extend.springboot.web;

import org.noear.solon.core.handle.Context;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Web 拦截器处理，为 Context 添加 result 便于日志
 *
 * <pre><code>
 * @Configuration
 * public class WebMvcConfigurerSolon implements WebMvcConfigurer {
 *     @Override
 *     public void addInterceptors(InterceptorRegistry registry) {
 *         registry.addInterceptor(new HandlerInterceptorSolon())
 *                 .addPathPatterns("/**");
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.5
 */
public class HandlerInterceptorSolon implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Context c = Context.current();
        if (c != null) {
            c.result = modelAndView;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {
        if (e != null) {
            Context c = Context.current();
            if (c != null) {
                c.errors = e;
            }
        }
    }
}
