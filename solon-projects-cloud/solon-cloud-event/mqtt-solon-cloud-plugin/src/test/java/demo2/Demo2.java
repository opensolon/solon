package demo2;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.extend.mqtt.service.CloudEventServiceMqtt3;
import org.noear.solon.cloud.impl.CloudEventServiceManager;

/**
 * @author noear 2023/7/9 created
 */
@Configuration
public class Demo2 {

    @Inject
    MqttClient mqttClient;
}
