package demo;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.noear.solon.annotation.Component;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.rx.integration.ActionReturnRxHandler;
import org.reactivestreams.Publisher;

/**
 * @author noear 2025/1/29 created
 */
@Component(index = -1)
public class Case2ReturnRxHandler extends ActionReturnRxHandler {
    @Override
    public boolean matched(Context ctx, Class<?> returnType) {
        return Multi.class.isAssignableFrom(returnType)
                || Uni.class.isAssignableFrom(returnType);
    }

    @Override
    protected Publisher postPublisher(Context ctx, Action action, Object result, boolean isStreaming) throws Throwable {
        if (result instanceof Multi) {
            if (ctx.acceptNew().startsWith(MimeType.APPLICATION_X_NDJSON_VALUE) == false) {
                return ((Multi) result).collect().asList().toMulti();
            }
        } else if (result instanceof Uni) {
            return ((Uni) result).toMulti();
        }

        return (Publisher) result;
    }
}
