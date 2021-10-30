package org.noear.solon.cloud.extend.mqtt;

import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class MqttProps {
    public static final String PROP_EVENT_CLIENTID = "event.clientId";

    public static final CloudProps instance = new CloudProps("mqtt");
}
