package webapp.demo1_handler;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/**
 * 简单的http处理
 * */
@XMapping("/demo1/run1/*")
@XController
public class Run1Handler implements XHandler {
    @Override
    public void handle(XContext cxt) throws Exception {
        cxt.output(cxt.url());
    }
}
