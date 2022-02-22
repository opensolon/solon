package com.fujieid.jap.solon;

import com.fujieid.jap.core.JapUserService;
import com.fujieid.jap.core.config.AuthenticateConfig;
import com.fujieid.jap.core.config.JapConfig;
import com.fujieid.jap.core.exception.JapException;
import com.fujieid.jap.core.exception.JapUserException;
import com.fujieid.jap.core.result.JapResponse;
import com.fujieid.jap.http.JapHttpRequest;
import com.fujieid.jap.http.JapHttpResponse;
import com.fujieid.jap.social.SocialConfig;
import com.fujieid.jap.social.SocialFunc;
import com.fujieid.jap.social.SocialStrategy;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthRequest;
import org.joor.Reflect;

/**
 * @author 颖
 * @since 1.6
 */
public class FixedSocialStrategy extends SocialStrategy {

    public FixedSocialStrategy(JapUserService japUserService, JapConfig japConfig) {
        super(japUserService, japConfig);
    }

    @Override
    public JapResponse authenticate(AuthenticateConfig config, JapHttpRequest request, JapHttpResponse response) {
        SocialConfig socialConfig = null;
        try {
            this.checkAuthenticateConfig(config, SocialConfig.class);
            socialConfig = (SocialConfig) config;
        } catch (JapException e) {
            return JapResponse.error(e.getErrorCode(), e.getErrorMessage());
        }

        if (socialConfig.isBindUser()) {
            return this.bind(config, request, response);
        }
        // !!! 取消 Session 校验 !!!
//        JapUser sessionUser = this.checkSession(request, response);
//        if (null != sessionUser) {
//            return JapResponse.success(sessionUser);
//        }

        AuthRequest authRequest = null;
        try {
            authRequest = Reflect.on(this).call("getAuthRequest", config).get();
        } catch (JapException e) {
            return JapResponse.error(e.getErrorCode(), e.getErrorMessage());
        }

        String source = socialConfig.getPlatform();

        AuthCallback authCallback = Reflect.on(this).call("parseRequest", request).get();

        if (Reflect.on(this).call("isCallback", source, authCallback).get()) {
            try {
                return Reflect.on(this).call("login", request, response, source, authRequest, authCallback, (SocialFunc) this::loginSuccess).get();
            } catch (JapUserException e) {
                return JapResponse.error(e.getErrorCode(), e.getErrorMessage());
            }
        }

        // If it is not a callback request, it must be a request to jump to the authorization link
        String url = authRequest.authorize(socialConfig.getState());
        return JapResponse.success(url);
    }

}
