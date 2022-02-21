package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.result.JapResponse;
import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.social.SocialConfig;
import com.fujieid.jap.social.SocialStrategy;
import com.fujieid.jap.solon.HttpServletRequestWrapperImpl;
import com.fujieid.jap.solon.JapProps;
import me.zhyd.oauth.utils.UuidUtils;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.data.cache.CacheService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 * @author work
 * @since 1.6
 */
public class SocialController extends JapController {

    @Inject
    JapProps japProperties;
    @Inject
    SocialStrategy socialStrategy;
    @Inject
    CacheService cacheService;

    /**
     * 第三方跳转方法
     */
    @Get
    @Mapping("/social/{platform}/redirect")
    public Object redirect(HttpServletRequest request, HttpServletResponse response, String platform, String callback) throws IllegalAccessException {
        // 验证 二次回调地址 是否合法
        if (!this.validCallback(callback)) {
            throw new IllegalAccessException();
        }

        // 构建 社会化登录 Payload
        SocialConfig socialConfig = new SocialConfig()
                .setPlatform(platform)
                .setState(UuidUtils.getUUID())
                .setJustAuthConfig(this.japProperties.getCredentials().get(platform));
        // 将 State -> Callback 存入缓存
        this.cacheService.store(
                this.getKey(socialConfig.getState()),
                callback,
                300
        );
        // 请求登录
        JapResponse japResponse = this.socialStrategy.authenticate(
                socialConfig,
                new JakartaRequestAdapter(new HttpServletRequestWrapperImpl(Context.current(), request)),
                new JakartaResponseAdapter(response)
        );

        return this.simpleResponse(japResponse);
    }

    /**
     * 第三方回调方法
     */
    @Get
    @Mapping("/social/{platform}/callback")
    public void callback(String platform, String state, String code) {
        String callback = (String) this.cacheService.get(
                this.getKey(state)
        );

        // 如果 Callback 所属的 State 已过期
        if (callback == null) {
            throw new IllegalStateException();
        }
        boolean hasParameters = callback.contains("?");

        // 拿到回调地址则跳转
        Context.current().redirect(callback + (hasParameters ? "&" : "?") + "code=" + code + "&state=" + state + "&platform=" + platform);
    }

    private String getKey(String state) {
        return String.format("%s:%s", this.getClass().getName(), state);
    }

}
