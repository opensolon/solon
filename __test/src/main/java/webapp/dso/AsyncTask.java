package webapp.dso;

import org.noear.solon.proxy.annotation.ProxyComponent;
import org.noear.solon.scheduling.annotation.Async;

/**
 * @author noear 2022/1/12 created
 */
@ProxyComponent
public class AsyncTask {
    @Async
    public void test(){
        System.out.println(Thread.currentThread().getName());
    }
}
