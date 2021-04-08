package org.noear.solon.cloud.extend.mqtt;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class MqttProps {
    public static final CloudProps instance = new CloudProps("mqtt");

    public static String clientId() {
        return Solon.cfg().get("solon.cloud.mqtt.event.clientId");
    }
}
