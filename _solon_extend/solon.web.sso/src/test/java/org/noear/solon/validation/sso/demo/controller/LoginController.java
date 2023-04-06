package org.noear.solon.validation.sso.demo.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.sso.SsoUtil;

/**
 * @author noear 2023/4/5 created
 */
@Controller
public class LoginController {

    @Mapping("/login")
    public void login(Context ctx){
        if (loginDo()) {
            //获取登录的用户id
            long userId = 1;

            //更新用户的单点登录标识
            SsoUtil.login(userId);
        }
    }

    /**
     * 执行真实的登录处理
     * */
    private boolean loginDo(){
        //...
        return true;
    }
}
