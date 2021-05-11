package validation.demo1;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author noear 2021/5/11 created
 */
@Data
//@ApiModel(value = "登录表单")
public class LoginForm {

//    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

//    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}