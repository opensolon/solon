/*
 * Copyright 2017-2024 noear.org and authors
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
