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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图形验证码数据[响应实体]
 *
 * @author 多仔ヾ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description= "图形验证码数据[响应实体]")
public class GpcCaptchaImageRespVO {

    /** 图形验证码id **/
    @ApiModelProperty(value = "图形验证码id", required = true)
    private String captchaId;

    /** 图形验证码base64值 **/
    @ApiModelProperty(value = "图形验证码base64值", required = true)
    private String captchaImg;

}
