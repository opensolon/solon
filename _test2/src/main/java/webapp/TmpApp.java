package webapp;

import org.noear.solon.XApp;

public class TmpApp {
    public static void main(String[] args) {
        XApp app = XApp.start(TmpApp.class, args);


        app.get("**",(c)-> {
            c.output("test");
        });
    }
}
