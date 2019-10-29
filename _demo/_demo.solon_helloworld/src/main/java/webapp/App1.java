package webapp;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;

/**
 * helloworld 1
 * */
@XController
public class App1 {
    public static void main(String[] args){
        XApp.start(App1.class,args);
    }

    @XMapping("/helloworld")
    public String helloworld(){
        return "hello world!";
    }
}
