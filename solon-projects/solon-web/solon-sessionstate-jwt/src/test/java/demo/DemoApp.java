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
package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

import java.util.Date;

/**
 * @author noear 2022/10/26 created
 */
@Controller
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);
    }

    @Mapping("get")
    public Object get(Context ctx) {
        return ctx.session("user", Object.class);
    }


    @Mapping("set")
    public void set(Context ctx) {
        User user = new User();
        user.id = 1;
        user.name = "wold";
        ctx.sessionSet("user", user);
        ctx.sessionSet("int", 1);
        ctx.sessionSet("long", 1L);
        ctx.sessionSet("str", "test");
        ctx.sessionSet("time", new Date());
        ctx.sessionSet("bool", true);
        ctx.sessionSet("flot", 0.12);
    }
}
