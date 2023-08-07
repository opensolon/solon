package demo;

import demo.dso.service.UserService;
import org.noear.solon.Solon;

/**
 * @author noear 2021/7/12 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);

        //test
        UserService userService = Solon.context().getBean(UserService.class);
        assert userService.getUserList() != null;
    }
}
