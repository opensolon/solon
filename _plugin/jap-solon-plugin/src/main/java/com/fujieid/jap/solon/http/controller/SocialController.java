package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.result.JapResponse;
import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.social.SocialConfig;
import com.fujieid.jap.solon.HttpServletRequestWrapperImpl;
import com.fujieid.jap.solon.JapProps;
import com.fujieid.jap.solon.JapSolonConfig;

import me.zhyd.oauth.utils.UuidUtils;

import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * //手动添加的控制器，不要用 @Controller 注解（免得重复）
 *
 * @author 颖
 * @since 1.6
 */
public class SocialController extends JapController {

    @Inject
    private JapProps japProperties;
    @Inject
    private JapSolonConfig japSolonConfig;

    /**
     * 第三方跳转方法
     */
    @Get
    @Mapping("social/{platform}/redirect")
    public Object redirect(HttpServletRequest request, HttpServletResponse response, String platform, String callback) throws IllegalAccessException {
        if (!this.japProperties.getCallbacks().contains(callback)) {
            throw new IllegalAccessException();
        }

        Context ctx = Context.current();
        request = new HttpServletRequestWrapperImpl(ctx, request);

        SocialConfig socialConfig = new SocialConfig()
                .setPlatform(platform)
                .setState(UuidUtils.getUUID())
                .setJustAuthConfig(this.japProperties.getCredentials().get(platform));

        JapResponse japResponse = this.japSolonConfig.getSocialStrategy().authenticate(
                socialConfig,
                new JakartaRequestAdapter(request),
                new JakartaResponseAdapter(response)
        );

        return this.simpleResponse(japResponse);
    }

    /**
     * 第三方回调方法
     */
    @Get
    @Mapping("social/{platform}/callback/")
    public void callback(Context ctx, String platform, String state, String code, String callback) {
        callback = callback.endsWith("/") ? callback : callback + "/";
        ctx.redirect(callback + "/code=" + code + "&state=" + state + "&platform=" + platform);
    }

}
