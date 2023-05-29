package org.noear.solon.web.cors;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.web.cors.annotation.CrossOrigin;

/**
 * 跨域处理
 *
 * @author noear
 * @since 1.3
 */
public class CrossHandler extends AbstractCross<CrossHandler> implements Handler {
    public CrossHandler() {

    }

    public CrossHandler(CrossOrigin anno) {
        maxAge(anno.maxAge());
        //支持表达式配置: ${xxx}
        allowedOrigins(Solon.cfg().getByParse(anno.origins()));
        allowCredentials(anno.credentials());
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        doHandle(ctx);
    }
}
