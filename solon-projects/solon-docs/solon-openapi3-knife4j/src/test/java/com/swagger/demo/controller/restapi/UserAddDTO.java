package com.swagger.demo.controller.restapi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

import java.io.Serializable;

@Data
@Schema(description = "添加用户表单")
public class UserAddDTO implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;

    private static final long serialVersionUID = -3855689131266589152L;
}