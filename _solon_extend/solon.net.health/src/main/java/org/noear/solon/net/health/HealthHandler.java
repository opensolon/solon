package org.noear.solon.net.health;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 检健检测代理（方便复用于别的检测路径）
 *
 * @author iYarnFog
 * @since 1.5
 */
public class HealthHandler implements Handler {

    /**
     * 参考代理路径
     */
    public static final String HANDLER_PATH = "/healthz";


    private static final HealthHandler instance = new HealthHandler();

    /**
     * 获取实例
     */
    public static HealthHandler getInstance() {
        return instance;
    }


    private static final Options options = Options.def().add(Feature.EnumUsingName);

    @Override
    public void handle(Context ctx) throws Throwable {
        HealthCheckResult result = HealthChecker.check();

        switch (result.getStatus()) {
            case DOWN:
                ctx.status(503);
                break;
            case ERROR:
                ctx.status(500);
                break;
            default:
                ctx.status(200);
        }

        ctx.outputAsJson(ONode.stringify(result, options));
    }
}
