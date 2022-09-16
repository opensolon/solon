package webapp.dso;

import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.extend.async.annotation.Async;

/**
 * @author noear 2022/1/12 created
 */
@Service
public class AsyncTask {
    @Async
    public void test(){
        System.out.println(Thread.currentThread().getName());
    }
}
