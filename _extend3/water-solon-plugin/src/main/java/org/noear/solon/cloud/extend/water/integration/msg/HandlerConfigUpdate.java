package org.noear.solon.cloud.extend.water.integration.msg;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.water.service.CloudConfigServiceImp;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear
 * @since 1.2
 */
public class HandlerConfigUpdate implements CloudEventHandler {
    private CloudConfigServiceImp configService;

    public HandlerConfigUpdate(CloudConfigServiceImp configService) {
        this.configService = configService;
    }

    @Override
    public boolean handler(Event event) {
        String[] tag_keys = event.getContent().split(";");


        for (String tagKey : tag_keys) {
            String[] ss = tagKey.split("::");

            if (ss.length > 1) {
                configService.onUpdate(ss[0], ss[1]);
            }
        }

        return true;
    }
}
