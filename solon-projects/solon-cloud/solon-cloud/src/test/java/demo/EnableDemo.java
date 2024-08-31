package demo;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;

/**
 * @author noear 2024/8/27 created
 */
public class EnableDemo {
    public static void main(String[] args) {
        Solon.start(EnableDemo.class, args, app -> {
            CloudClient.enableEvent(false);
        });
    }
}
