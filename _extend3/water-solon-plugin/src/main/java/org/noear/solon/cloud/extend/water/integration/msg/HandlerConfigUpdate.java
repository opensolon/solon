package org.noear.solon.cloud.extend.water.integration.msg;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.model.Event;
import org.noear.water.WaterClient;

/**
 * @author noear
 * @since 1.2
 */
public class HandlerConfigUpdate implements CloudEventHandler {
    @Override
    public boolean handler(Event event) {
        String[] tag_keys = event.content.split(";");


        for (String tagKey : tag_keys) {
            String[] ss = tagKey.split("::");

            if (ss.length > 1) {
                WaterClient.Config.reload(ss[0]);
                this.configUpdateHandler(ss[0], ss[1]);
            }
        }

        return true;
    }

    public void configUpdateHandler(String tag, String name) {
    }
}
