package validation.demo1;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.jsr303.ValidationUtils;

/**
 * @author noear 2021/5/11 created
 */
@Mapping("/api")
@Controller
public class LoginController {

    @Mapping("login")
    public Result login(LoginForm loginForm) {
        System.out.println("已进入login" + loginForm);

        ValidationUtils.validate(loginForm);


        return Result.succeed();
    }

    public static void main(String[] args) {
        Solon.start(LoginController.class, args);

        if(Solon.cfg() == null){
            return;
        }
    }
}