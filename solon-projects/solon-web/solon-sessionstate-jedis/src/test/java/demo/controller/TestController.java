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
package demo.controller;

import demo.model.UserModel;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2022/1/18 created
 */
@Controller
public class TestController {
    @Mapping("put")
    public String put(Context ctx) {
        UserModel user = new UserModel();
        user.id = 12;
        user.name = "world";

        ctx.sessionSet("user", user);
        return "OK";
    }

    @Mapping("get")
    public UserModel get(Context ctx) {
        UserModel tmp = ctx.session("user", UserModel.class);

        return tmp;
    }
}
