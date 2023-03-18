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
