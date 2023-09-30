package webapp.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.scheduling.annotation.Async;

/**
 * @author noear 2022/1/12 created
 */
@Component
public class AsyncTask {
    @Async
    public void test() {
        System.out.println("++++++++++" + Thread.currentThread().getName());
    }
}
