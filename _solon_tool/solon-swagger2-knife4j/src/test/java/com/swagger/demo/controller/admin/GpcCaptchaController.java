package com.swagger.demo.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * 行为验证[网关接口]
 *
 * @author 多仔ヾ
 */
@Controller
@Mapping("/gpcGateway/captcha")
@Api(tags = "行为验证")
public class GpcCaptchaController {

    /**
     * 生成图形验证码
     *
     * @author 多仔ヾ
     * @return cn.echostack.comn.core.domain.ResponseResult<cn.echostack.module.gpc.captcha.domain.GpcCaptchaImageRespVO>
     **/
    @Mapping("image")
    @Get
    @ApiOperation(value = "生成图形验证码")
    public ResponseResult<GpcCaptchaImageRespVO> getImageCaptcha() {
        return null;
    }

}
