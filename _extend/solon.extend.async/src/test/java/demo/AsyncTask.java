package demo;

import org.noear.solon.extend.aspect.annotation.Service;
import org.noear.solon.extend.async.annotation.Async;

/**
 * @author noear 2022/1/15 created
 */
@Service
public class AsyncTask {
    @Async
    public void snedMail(String mail, String title) {

    }
}
