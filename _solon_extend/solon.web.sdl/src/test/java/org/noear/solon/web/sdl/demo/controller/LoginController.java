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
