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
package webapp.demob_session;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

import java.util.Date;

@Controller
public class Test {
    @Mapping("/demob/session/setval")
    public void setVal(Context ctx) {
        ctx.sessionSet("v1", new Date());
        ctx.sessionSet("v2", "我是字符串");
        ctx.sessionSet("v3", 121212L);
    }

    @Mapping(value = "/demob/session/getval", produces = "text/html;charset=utf-8")
    public void getVal(Context ctx) {
        Object v1 = ctx.session("v1", Object.class);
        Object v2 = ctx.session("v2", Object.class);
        Object v3 = ctx.session("v3", Object.class);

        ctx.output(v1 + "<br/>");
        ctx.output(v2 + "<br/>");
        ctx.output(v3 + "<br/>");
    }
}
