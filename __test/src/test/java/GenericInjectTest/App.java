package GenericInjectTest;

import org.noear.solon.Solon;

/**
 * @author noear 2022/10/27 created
 */
public class App {
    public static void main(String[] args){
        Solon.start(App.class, args);

        UserController controller = Solon.context().getBean(UserController.class);
        UserService userService = controller.getService();

        assert userService != null;
    }
}
