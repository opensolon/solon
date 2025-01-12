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
package webapp.demoa_interceptor;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;


//@Controller
public class BeforeHandlerBean {
    public static class Before1 implements Filter {

        @Override
        public void doFilter(Context ctx, FilterChain chain) throws Throwable {
            ctx.output("XInterceptor1::你被我拦截了(/demoa/**)!!!\n");
            chain.doFilter(ctx);
        }
    }

    public static class Before2 implements Filter{

        @Override
        public void doFilter(Context ctx, FilterChain chain) throws Throwable {
            ctx.output("XInterceptor2::你被我拦截了(/demoa/**)!!!\n");
            chain.doFilter(ctx);
        }
    }

    public static class Before3 implements Filter{

        @Override
        public void doFilter(Context ctx, FilterChain chain) throws Throwable {
            ctx.output("XInterceptor3::你被我拦截了(/demoa/**)!!!\n");
            chain.doFilter(ctx);
        }
    }

    //@Mapping(value = "/demoa/**",index = 1, endpoint = Endpoint.before)
    public void call(Context context, String sev) {
        context.output("XInterceptor1::你被我拦截了(/demoa/**)!!!\n");
    }

    //@Mapping(value = "/demoa/**",index = 3, endpoint = Endpoint.before)
    public void call2(Context context, String sev) {
        context.output("XInterceptor3::你被我拦截了(/demoa/**)!!!\n");
    }

    //@Mapping(value = "/demoa/**",index = 2, endpoint = Endpoint.before)
    public void call3(Context context, String sev) {
        context.output("XInterceptor2::你被我拦截了(/demoa/**)!!!\n");
    }

}
