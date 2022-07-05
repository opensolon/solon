package features;

import org.noear.solon.Solon;
import org.noear.solon.guard.GuardUtils;

/**
 * @author noear 2022/7/5 created
 */
public class TestApp {
    public static void main(String[] args) throws Exception{
        Solon.start(TestApp.class, args);

        //System.out.println(GuardUtils.encrypt("abc"));
        //System.out.println(GuardUtils.encrypt("123456"));
    }
}
