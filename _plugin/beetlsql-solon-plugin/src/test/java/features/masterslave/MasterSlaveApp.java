package features.masterslave;


import org.noear.solon.Solon;

public class MasterSlaveApp {
    public static void main(String[] args) {
        Solon.start(MasterSlaveApp.class, args, (app) -> {
            app.cfg().loadAdd("application-master-slave.properties");
        });
    }
}
