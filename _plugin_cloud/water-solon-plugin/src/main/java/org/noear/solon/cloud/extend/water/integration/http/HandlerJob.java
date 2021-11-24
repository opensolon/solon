package org.noear.solon.cloud.extend.water.integration.http;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.extend.water.service.CloudJobServiceWaterImp;
import org.noear.solon.cloud.model.JobHandlerEntity;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 任务调度处理（用令牌的形式实现安全）
 *
 * @author noear
 * @since 1.4
 */
public class HandlerJob implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        String ip = ctx.realIp();
        String token = ctx.header("token", "");

        //订时任务，必须要有令片
        if (authServerSafe(ip, token)) {
            ctx.status(400);
            ctx.output("Invalid token!");
            return;
        }


        handleDo(ctx, ctx.param("name"));
    }

    private void handleDo(Context ctx, String name) {
        JobHandlerEntity entity = CloudJobServiceWaterImp.instance.get(name);

        if (entity == null) {
            ctx.status(400);
            if (Utils.isEmpty(name)) {
                ctx.output("CloudJob need the name parameter");
            } else {
                ctx.output("CloudJob[" + name + "] no exists");
            }
        } else {
            try {
                entity.getHandler().handle(ctx);
                ctx.output("OK");
            } catch (Throwable ex) {
                ex = Utils.throwableUnwrap(ex);
                EventBus.push(ex);
                ctx.status(500);
                ctx.output(ex);
            }
        }
    }

    private boolean authServerSafe(String ip, String token) {
        if (Solon.cfg().isDriftMode()) {
            return true;
        } else {
            return CloudClient.list().inListOfServerToken(token) ||
                    CloudClient.list().inListOfServerIp(ip);

        }
    }
}