package webapp.demoa_interceptor;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

public class BeforeHandler implements Handler {
    @Override
    public void handle(Context c) throws Throwable {
        c.attrSet("_start",System.currentTimeMillis());
    }
}
