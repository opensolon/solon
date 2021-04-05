package org.noear.solon.cloud.extend.mqtt.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventService;

/**
 * @author noear 2021/4/4 created
 */
public class CloudEventServiceImp implements CloudEventService {
    //
    // 1833(MQTT的默认端口号)
    //
    public CloudEventServiceImp(String server){

    }

    @Override
    public boolean publish(Event event) {
        return false;
    }

    @Override
    public void attention(EventLevel level, String group, String topic, CloudEventHandler observer) {

    }

    public void subscribe() {

    }
}
