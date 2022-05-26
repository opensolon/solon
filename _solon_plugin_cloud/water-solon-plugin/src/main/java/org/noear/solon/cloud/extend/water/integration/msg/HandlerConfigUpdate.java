package org.noear.solon.cloud.extend.water.integration.msg;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.extend.water.service.CloudConfigServiceWaterImp;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear
 * @since 1.2
 */
public class HandlerConfigUpdate implements CloudEventHandler {
    private CloudConfigServiceWaterImp configService;

    public HandlerConfigUpdate(CloudConfigServiceWaterImp configService) {
        this.configService = configService;
    }

    @Override
    public boolean handle(Event event) {
        String[] tagKeyAry = event.content().split(";");

        for (String tagKey : tagKeyAry) {
            String[] ss = null;
            if (tagKey.contains("::")) {
                ss = tagKey.split("::");
            } else {
                ss = tagKey.split(":");
            }

            if (ss.length > 1) {
                configService.onUpdate(ss[0], ss[1]);
            }
        }

        return true;
    }
}
