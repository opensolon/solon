package validation.demo1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.extend.jsr303.ValidationUtils;
import org.noear.solon.test.SolonJUnit4ClassRunner;

/**
 * @author noear 2021/5/11 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class LoginTest {
    @Test
    public void login() throws Exception{
        LoginForm loginForm = new LoginForm();
        loginForm.setMobile("xxx");

        System.out.println("已进入login" + loginForm);

        ValidationUtils.validate(loginForm);
    }
}
