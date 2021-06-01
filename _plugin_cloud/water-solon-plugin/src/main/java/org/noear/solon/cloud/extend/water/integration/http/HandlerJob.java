package org.noear.solon.cloud.extend.water.integration.http;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.extend.water.service.CloudJobServiceWaterImp;
import org.noear.solon.cloud.model.HandlerEntity;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear
 * @since 1.4
 */
public class HandlerJob implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        String ip = ctx.realIp();

        if (CloudClient.list().inListOfClientAndServerIp(ip)) {
            handleDo(ctx, ctx.param("name"));
        } else {
            ctx.output((ip + ",not is whitelist!"));
        }
    }

    private void handleDo(Context ctx, String name) {
        HandlerEntity entity = CloudJobServiceWaterImp.instance.get(name);

        if (entity == null) {
            ctx.statusSet(400);
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
                ctx.statusSet(500);
                ctx.output(ex);
            }
        }
    }
}