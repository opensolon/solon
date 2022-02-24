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
    @Mapping("/social/{platform}")
    public Object redirect(Context ctx, HttpServletRequest request, HttpServletResponse response, String platform, String next, String code, String state) throws IllegalAccessException {
        // 验证 二次回调地址 是否合法
        if (next == null) {
            // 如果没指定回调地址，可能是第三方回调的结果
            next = (String) this.cacheService.get(
                    this.getKey(state)
            );
            // 如果 Callback 所属的 State 已过期
            if (next == null) {
                throw new IllegalStateException();
            } else {
                // 填入缺失的 next 参数
                ctx.paramSet("next", next);
            }
        } else {
            if (!this.validNext(next)) {
                throw new IllegalAccessException();
            }
        }

        // 构建 社会化登录 Payload
        SocialConfig socialConfig = new SocialConfig()
                .setPlatform(platform)
                .setState(UuidUtils.getUUID())
                .setJustAuthConfig(this.japProperties.getCredentials().get(platform));
        // 将 State -> Callback 存入缓存
        this.cacheService.store(
                this.getKey(socialConfig.getState()),
                next,
                300
        );
        // 请求登录
        JapResponse japResponse = this.socialStrategy.authenticate(
                socialConfig,
                new JakartaRequestAdapter(new HttpServletRequestWrapperImpl(ctx, request)),
                new JakartaResponseAdapter(response)
        );

        return this.simpleResponse(ctx, japResponse);
    }

    private String getKey(String state) {
        return String.format("%s:%s", this.getClass().getName(), state);
    }
}
