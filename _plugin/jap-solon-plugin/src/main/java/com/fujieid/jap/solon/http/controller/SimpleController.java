package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.JapUser;
import com.fujieid.jap.core.result.JapResponse;
import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.simple.SimpleConfig;
import com.fujieid.jap.solon.HttpServletRequestWrapperImpl;
import com.fujieid.jap.solon.JapProps;
import com.fujieid.jap.solon.JapSolonConfig;

import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * //手动添加的控制器，不要用 @Controller 注解（免得重复）
 * */
public class SimpleController {

    @Inject
    private JapProps japProperties;
    @Inject
    private JapSolonConfig japSolonConfig;

    @Post
    @Mapping("/login")
    public Object login(HttpServletRequest request, HttpServletResponse response) {
        request = new HttpServletRequestWrapperImpl(Context.current(), request);

        SimpleConfig simple = this.japProperties.getSimple();
        JapResponse japResponse = this.japSolonConfig.getSimpleStrategy()
                .authenticate(new SimpleConfig()
                                .setUsernameField(simple.getUsernameField())
                                .setPasswordField(simple.getPasswordField())
                                .setCredentialEncryptSalt(simple.getCredentialEncryptSalt())
                                .setRememberMeField(simple.getRememberMeField())
                                .setRememberMeCookieKey(simple.getRememberMeCookieKey())
                                .setRememberMeCookieExpire(simple.getRememberMeCookieExpire())
                                .setRememberMeCookieDomain(simple.getRememberMeCookieDomain()),
                        new JakartaRequestAdapter(request),
                        new JakartaResponseAdapter(response));

        if (!japResponse.isSuccess()) {
            if (this.japProperties.isSeparate()) {
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
            if (this.japProperties.isSeparate()) {
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
