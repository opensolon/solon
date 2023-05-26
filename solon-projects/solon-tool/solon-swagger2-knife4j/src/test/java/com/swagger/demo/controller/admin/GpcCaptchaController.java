package com.swagger.demo.controller.admin;

import com.swagger.demo.model.bean.Tree;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import java.util.List;

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
     * @return cn.echostack.comn.core.domain.ResponseResult<cn.echostack.module.gpc.captcha.domain.GpcCaptchaImageRespVO>
     * @author 多仔ヾ
     **/
    @Mapping("image")
    @Get
    @ApiOperation(value = "生成图形验证码")
    public ResponseResult<GpcCaptchaImageRespVO> getImageCaptcha() {
        return null;
    }

    @Mapping("image2")
    @ApiOperation(value = "生成图形验证码2")
    public ResponseResult<String> getImageCaptcha2() {
        return null;
    }

    @Mapping("image3")
    @ApiOperation(value = "生成图形验证码3")
    public ResponseResult<List<GpcCaptchaImageRespVO>> getImageCaptcha3() {
        return null;
    }

    @Mapping("image4")
    @ApiOperation(value = "生成图形验证码4")
    public ResponseResult<List<Tree<GpcCaptchaImageRespVO>>> getImageCaptcha4() {
        return null;
    }
}
