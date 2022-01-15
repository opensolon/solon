package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2022/1/15 created
 */
@Controller
public class DemoController {
    @Inject
    AsyncTask asyncTask;

    @Mapping("test")
    public void test(String mail, String title) {
        asyncTask.snedMail(mail, title);
    }
}
