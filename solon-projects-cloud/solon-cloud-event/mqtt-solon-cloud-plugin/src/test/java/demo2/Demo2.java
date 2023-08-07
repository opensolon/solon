package demo2;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.extend.mqtt.service.CloudEventServiceMqttImp;
import org.noear.solon.cloud.impl.CloudEventServiceManager;

/**
 * @author noear 2023/7/9 created
 */
@Configuration
public class Demo2 {
    @Bean
    public MqttClient getMqttClient() {
        //
        //理论上，是不允许这么干的
        //
        CloudEventServiceManager eventServiceManager = (CloudEventServiceManager) CloudClient.event();
        //后面 get("") 参数名是 channel //没用多个 solon cloud event 插件同时用时，一般不会配置
        CloudEventServiceMqttImp eventService = (CloudEventServiceMqttImp) eventServiceManager.get("");
        return eventService.getClient();
    }
}
