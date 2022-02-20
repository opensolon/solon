package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.JapUser;
import com.fujieid.jap.core.result.JapResponse;
import com.fujieid.jap.solon.JapProps;
import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;

/**
 * @author 颖
 */
public class JapController {

    public Object simpleResponse(JapResponse japResponse) {
        JapProps japProperties= Aop.get(JapProps.class);

        if (!japResponse.isSuccess()) {
            if (japProperties.isSeparate()) {
                return japResponse;
            } else {
                // Todo: 普通项目跳转
                return null;
            }
        }
        if (japResponse.isRedirectUrl()) {
            Context.current().redirect((String) japResponse.getData());
            return null;
        } else {
            // 登录成功，需要对用户数据进行处理
            if (japProperties.isSeparate()) {
                JapUser japUser = (JapUser) japResponse.getData();
                japUser.setPassword(null);
                return japUser;
            } else {
                // Todo: 普通项目跳转
                return null;
            }
        }
    }

}
