package webapp.models;

import lombok.Data;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 注册账号实体类
 * @Author: 李涵祥
 * @Date: 2022/5/18
 */
@Data
public class RegisterUsername extends LoginUsername{

    @NotBlank(message = "请输入密码")
    @Length(min = 6, max = 16, message = "密码最少6位最多16位")
    private String confirmPassword;


}
