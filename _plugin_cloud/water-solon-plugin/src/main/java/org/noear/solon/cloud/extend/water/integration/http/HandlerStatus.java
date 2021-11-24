package org.noear.solon.cloud.extend.water.integration.http;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.water.WaterClient;
import org.noear.water.utils.RuntimeStatus;
import org.noear.water.utils.RuntimeUtils;

/**
 * 运行状态获取处理（用ip名单处理安全）
 *
 * @author noear
 * @since 1.2
 */
public class HandlerStatus implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        String ip = ctx.realIp();

        if (authServerIp(ip)) {
            RuntimeStatus rs = RuntimeUtils.getStatus();
            rs.name = Instance.local().service();
            rs.address = Instance.local().address();

            ctx.outputAsJson(ONode.stringify(rs));
        } else {
            ctx.output((ip + ", not is safelist!"));
        }
    }

    private boolean authServerIp(String ip) {
        if (Solon.cfg().isDriftMode()) {
            return true;
        } else {
            return WaterClient.Whitelist.existsOfMasterIp(ip);
        }
    }
}