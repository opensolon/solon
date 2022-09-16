package webapp.models;

import lombok.Data;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 账号登录实体类
 * @Author: 李涵祥
 * @Date: 2022/5/16
 */
@Data
public class LoginUsername {

    @NotBlank(message = "请输入用户名")
    @Length(min = 6, max = 16, message = "用户名最少6位最多16位")
    private String username;

    @NotBlank(message = "请输入密码")
    @Length(min = 6, max = 16, message = "密码最少6位最多16位")
    private String password;

    @NotNull(message = "请同意站点隐私政策 和 站点协议")
    private Boolean privacy;

}
