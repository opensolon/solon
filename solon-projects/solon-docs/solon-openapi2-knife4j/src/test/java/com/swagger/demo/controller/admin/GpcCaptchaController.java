/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
