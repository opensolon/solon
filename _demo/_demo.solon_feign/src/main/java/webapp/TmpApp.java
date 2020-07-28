package webapp;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;

@XController
public class TmpApp {
    public static void main(String[] args) {
        XApp.start(TmpApp.class, args);
    }

    @XMapping("/")
    public Object helloworld(){
        return "Hello world";
    }
}
