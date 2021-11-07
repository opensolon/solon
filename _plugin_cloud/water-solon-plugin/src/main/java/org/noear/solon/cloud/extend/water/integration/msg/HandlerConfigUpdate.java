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
    public boolean handler(Event event) {
        String[] tag_keys = event.content().split(";");

        for (String tagKey : tag_keys) {
            String[] ss = tagKey.split(WaterProps.GROUP_SPLIT_MART);

            if (ss.length > 1) {
                configService.onUpdate(ss[0], ss[1]);
            }
        }

        return true;
    }
}
