package demo.webrx;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.noear.solon.annotation.Managed;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.rx.integration.RxReturnValueHandler;
import org.reactivestreams.Publisher;

/**
 * @author noear 2025/1/29 created
 */
@Managed(index = -1)
public class Case2ReturnRxHandler extends RxReturnValueHandler {
    @Override
    public boolean matched(Context ctx, Class<?> returnType) {
        return Multi.class.isAssignableFrom(returnType)
                || Uni.class.isAssignableFrom(returnType);
    }

    @Override
    protected Publisher postPublisher(Context ctx, Object result, boolean isStreaming) throws Throwable {
        if (result instanceof Multi) {
            if (isStreaming == false) {
                return ((Multi) result).collect().asList().toMulti();
            }
        } else if (result instanceof Uni) {
            return ((Uni) result).toMulti();
        }

        return (Publisher) result;
    }
}
