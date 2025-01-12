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

@Mapping(path = "/demo2/session")
@Controller
public class SessionController {
    @Mapping("id")
    public String id(Context ctx) {
        return ctx.sessionId();
    }

    @Mapping("set")
    public void set(Context ctx, String val) {
        ctx.sessionSet("val", val);
    }

    @Mapping("get")
    public Object get(Context ctx) {
        return ctx.session("val", Object.class);
    }


    @Mapping("token_err")
    public Object token_err(Context ctx) {
        try {
            ctx.session("val", Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ctx.session("val", Object.class);

        return "ok";
    }
}
