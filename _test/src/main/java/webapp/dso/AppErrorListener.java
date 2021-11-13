package webapp.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2020/12/17 created
 */
@Component
public class AppErrorListener implements EventListener<Throwable> {
    @Override
    public void onEvent(Throwable throwable) {
        Context ctx = Context.current();
        if(ctx != null){
            //如果当前有处理上下文，可以拿到请求路径，或者参数什么的...
            //
            ctx.path();
        }
    }
}
