package org.noear.solon.cloud.extend.health;

import org.noear.snack.ONode;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 检健检测代理（方便复用于别的检测路径）
 *
 * @author iYarnFog
 * @since 1.5
 */
public class HealthHandler implements Handler {
    private static final HealthHandler instance = new HealthHandler();

    /**
     * 获取实例
     * */
    public static HealthHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        HealthStatus healthStatus = HealthChecker.check();

        switch (healthStatus.getCode()) {
            case DOWN:
                ctx.status(503);
                break;
            case ERROR:
                ctx.status(500);
                break;
            default:
                ctx.status(200);
        }

        ctx.outputAsJson(ONode.stringify(healthStatus));
    }
}
