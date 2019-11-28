package webapp.demo1_handler;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/**
 * 简单的http处理
 * */
@XMapping("/demo1/run0/")
@XController
public class Run0Handler implements XHandler {
    @Override
    public void handle(XContext cxt) throws Exception {
        if(cxt.param("str") == null) {
            cxt.output("是null");
        }else{
            cxt.output("不是null(ok)");
        }
    }
}
