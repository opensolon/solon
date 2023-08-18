package demo2;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/7/9 created
 */
@Configuration
public class Demo2 {

    @Inject
    MqttClient mqttClient;
}
