package demo2;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/7/9 created
 */
@Component
public class Demo2 {

    @Inject
    MqttClient mqttClient;
}
