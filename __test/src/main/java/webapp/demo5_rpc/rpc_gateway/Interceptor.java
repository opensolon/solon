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
package webapp.demo5_rpc.rpc_gateway;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;


//@After({Interceptor.ApiIntercepter.class})
//@Before({ Interceptor.AuthInterceptor.class})
//@Controller
public class Interceptor {

    //@Mapping(value = "/demo52/**",index = 1, endpoint = Endpoint.before)
    public void call(Context context, String sev) {
        context.output("XInterceptor1，你被我拦截了(/{sev}/**)!!!\n");
    }

    //@Mapping(value = "/demo52/**",index = 3, endpoint = Endpoint.before)
    public void call2(Context context, String sev) {
        context.output("XInterceptor3，你被我拦截了(/{sev}/**)!!!\n");
    }

    //@Mapping(value = "/demo52/**",index = 2, endpoint = Endpoint.before)
    public void call3(Context context, String sev) {
        context.output("XInterceptor2，你被我拦截了(/{sev}/**)!!!\n");
    }

    //demo 省事儿，直接发写这儿
    public static class ApiIntercepter implements Handler {
        @Override
        public void handle(Context context) throws Throwable {
            context.output("XInterceptor of XBefore:API\n");
        }
    }

    //demo 省事儿，直接发写这儿
    public static class AuthInterceptor implements Handler {
        @Override
        public void handle(Context context) throws Throwable {
            context.output("XInterceptor of XAfter:Auth\n");
        }
    }
}
