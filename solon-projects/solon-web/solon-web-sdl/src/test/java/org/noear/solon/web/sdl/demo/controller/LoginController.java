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
package org.noear.solon.web.sdl.demo.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.sdl.SdlUtil;

/**
 * @author noear 2023/4/5 created
 */
@Controller
public class LoginController {

    @Mapping("/login")
    public String login(Context ctx) {
        if (loginDo()) {
            //获取登录的用户id
            long userId = 1;

            //更新用户的单点登录标识
            SdlUtil.login(userId);

            return "OK";
        }

        return "ERROR";
    }

    @Mapping("logout")
    public String logout() {
        SdlUtil.logout();
        return "OK";
    }

    /**
     * 执行真实的登录处理
     */
    private boolean loginDo() {
        //...
        return true;
    }
}
