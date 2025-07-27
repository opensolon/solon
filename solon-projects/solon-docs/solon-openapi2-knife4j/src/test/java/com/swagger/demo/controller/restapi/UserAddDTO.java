package com.swagger.demo.controller.restapi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

import java.io.Serializable;

@Data
@ApiModel(description = "添加用户表单")
public class UserAddDTO implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty("用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty("密码")
    private String password;

    private static final long serialVersionUID = -3855689131266589152L;
}