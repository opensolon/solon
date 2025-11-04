package features.smarthttp;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 *
 * @author noear 2025/11/4 created
 *
 */
@Mapping("ct0")
@Component
public class ContentTypeHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        ctx.output("hello");
    }
}
