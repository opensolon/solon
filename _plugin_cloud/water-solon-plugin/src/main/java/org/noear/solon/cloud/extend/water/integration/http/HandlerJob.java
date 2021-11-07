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
 * 任务处理者实体
 *
 * @author noear
 * @since 1.4
 */
public class HandlerJob implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        String ip = ctx.realIp();

        if (Solon.cfg().isWhiteMode()) {
            if (CloudClient.list().inListOfClientAndServerIp(ip) == false) {
                ctx.output(ip + ", not is whitelist!");
                return;
            }
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
}