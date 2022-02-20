package com.fujieid.jap.solon.controller;

import com.fujieid.jap.core.JapUser;
import com.fujieid.jap.core.result.JapResponse;
import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.simple.SimpleConfig;
import com.fujieid.jap.solon.HttpServletRequestWrapperImpl;
import com.fujieid.jap.solon.JapProps;
import com.fujieid.jap.solon.JapSolonConfig;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SimpleController {

    @Inject
    private JapProps japProperties;

    @Inject
    private JapSolonConfig japSolonConfig;

    @Post
    @Mapping("/username")
    public Object username(HttpServletRequest request, HttpServletResponse response) {
        request = new HttpServletRequestWrapperImpl(Context.current(), request);
        SimpleConfig simple = japProperties.getSimple();
        JapResponse japResponse = japSolonConfig.getSimpleStrategy().authenticate(new SimpleConfig()
                        .setUsernameField(simple.getUsernameField())
                        .setPasswordField(simple.getPasswordField())
                        .setCredentialEncryptSalt(simple.getCredentialEncryptSalt())
                        .setRememberMeField(simple.getRememberMeField())
                        .setRememberMeCookieKey(simple.getRememberMeCookieKey())
                        .setRememberMeCookieExpire(simple.getRememberMeCookieExpire())
                        .setRememberMeCookieDomain(simple.getRememberMeCookieDomain()),
                new JakartaRequestAdapter(request),
                new JakartaResponseAdapter(response));
        if (japResponse.isSuccess()) {
            JapUser japUser = (JapUser) japResponse.getData();
            japUser.setPassword(null);
            return japUser;
        }
        return japResponse.getData();
    }

}
