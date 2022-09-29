package org.noear.solon.cloud.extend.water.integration.http;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.water.utils.RuntimeStatus;
import org.noear.water.utils.RuntimeUtils;

/**
 * 运行状态获取处理（用令牌的形式实现安全）//较高频
 *
 * @author noear
 * @since 1.2
 */
public class HandlerStatus implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        String token = ctx.header(WaterProps.http_header_token, "");

        //调用任务必须要有server token
        if (authServerToken(token)) {
            RuntimeStatus rs = RuntimeUtils.getStatus();
            rs.name = Instance.local().service();
            rs.address = Instance.local().address();

            ctx.outputAsJson(ONode.stringify(rs));
        } else {
            ctx.status(400);
            ctx.output("Invalid server token!");
        }
    }

    /**
     * 验证安全性（基于token）
     */
    private boolean authServerToken(String token) {
        return CloudClient.list().inListOfServerToken(token);
    }
}