package com.anji.captcha.controller;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.5
 */
@Controller
@Mapping("/captcha")
public class CaptchaController {
    @Inject
    private CaptchaService captchaService;

    @Post
    @Mapping("/get")
    public ResponseModel get(CaptchaVO data, Context request) {
        assert request.realIp() != null;

        data.setBrowserInfo(getRemoteId(request));
        return this.captchaService.get(data);
    }

    @Post
    @Mapping("/check")
    public ResponseModel check(CaptchaVO data, Context request) {
        data.setBrowserInfo(getRemoteId(request));
        return this.captchaService.check(data);
    }

    public ResponseModel verify( CaptchaVO data, Context request) {
        return this.captchaService.verification(data);
    }

    public static final String getRemoteId(Context ctx) {
        return ctx.realIp() + ctx.userAgent();
    }
}
