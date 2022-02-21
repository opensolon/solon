package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.JapUser;
import com.fujieid.jap.core.result.JapResponse;
import org.noear.solon.core.handle.Context;

/**
 * @author 颖
 * @since 1.6
 */
public abstract class JapController {

    public Object simpleResponse(JapResponse japResponse) {
        String callback = Context.current().param("callback");
        boolean isSeparate = callback == null;

        if (!japResponse.isSuccess()) {
            if (isSeparate) {
                return japResponse;
            } else {
                // Todo: 普通项目数据储存
                Context.current().redirect(callback);
                return null;
            }
        }

        if (japResponse.isRedirectUrl()) {
            Context.current().redirect((String) japResponse.getData());
            return null;
        } else {
            // 登录成功，需要对用户数据进行处理
            if (isSeparate) {
                JapUser japUser = (JapUser) japResponse.getData();
                japUser.setPassword(null);
                return japUser;
            } else {
                // Todo: 普通项目数据储存
                Context.current().redirect(callback);
                return null;
            }
        }
    }

    protected boolean validCallback(String callback) {
        return true;
    }

}
