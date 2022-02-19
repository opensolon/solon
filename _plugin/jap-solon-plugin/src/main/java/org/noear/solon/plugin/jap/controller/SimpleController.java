package org.noear.solon.plugin.jap.controller;

import com.fujieid.jap.core.JapUser;
import com.fujieid.jap.core.result.JapResponse;
import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.simple.SimpleConfig;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;
import org.noear.solon.plugin.jap.config.httpSession.HttpServletRequestWrapperImpl;
import org.noear.solon.plugin.jap.properties.JapProperties;
import org.noear.solon.plugin.jap.properties.JapSoloConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Mapping("/login")
public class SimpleController {

    @Inject
    private JapProperties japProperties;

    @Inject
    private JapSoloConfig japSoloConfig;

    @Post
    @Mapping("/username")
    public Object username( HttpServletRequest request, HttpServletResponse response) {
        request = new HttpServletRequestWrapperImpl(Context.current(), request);
        SimpleConfig simple = japProperties.getSimple();
        JapResponse japResponse = japSoloConfig.getSimpleStrategy().authenticate(new SimpleConfig()
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
