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
package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

@Controller
public class HeaderController {
    @Mapping("/demo2/header/")
    public String header(Context ctx) throws Exception {
        return ctx.header("Water-Trace-Id");
    }

    @Mapping("/demo2/header2/")
    public String[] header2(Context ctx) throws Exception {
        return ctx.headerValues("test");
    }

    @Mapping("/demo2/remote/")
    public Object[] remote(Context ctx) throws Exception {
        return new Object[]{ctx.remoteIp(), ctx.remotePort()};
    }

    @Mapping("/demo2/cookie/")
    public void cookie(Context ctx) throws Exception {
        ctx.cookieSet("cookie1", "1");
        ctx.cookieSet("cookie2", "2");
    }

    @Mapping("/demo2/redirect/")
    public void redirect(Context ctx) throws Exception {
        ctx.redirect("/demo2/redirect/page");
    }

    @Mapping("/demo2/redirect/page")
    public String redirect_page(Context ctx) throws Exception {
        return "我是跳转过来的!";
    }

    @Mapping("/demo2/header/ct")
    public String header_ct(Context ctx, String name) throws Exception {
        return ctx.method() + "::" + ctx.contentType() + "::" + name;
    }
}
