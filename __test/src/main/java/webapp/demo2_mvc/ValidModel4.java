package webapp.demo2_mvc;

import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;


/**
 * @author noear 2021/5/11 created
 */
@Data
public class ValidModel4 {
    @NotBlank(message = "Id不能为空" , groups = UpdateModel.class)
    private String id;

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "密码不能为空")
    private String password;
}
