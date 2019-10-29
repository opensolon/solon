package webapp;

import org.noear.solon.XApp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class App {
    public static void main(String[] args) {
        XApp.start(App.class, args);

        xxx("2019-11-11T11:11:11");
    }

    public static Date xxx(String val){
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date tm = fm.parse(val);

            return tm;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
