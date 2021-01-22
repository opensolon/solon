package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.model.Instance;
import org.noear.water.WW;
import org.noear.water.WaterClient;
import org.noear.water.utils.EncryptUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author noear 2021/1/22 created
 */
public class CloudEventObservers {
    private static CloudEventObservers instance = new CloudEventObservers();

    public static CloudEventObservers getInstance() {
        return instance;
    }



    /**
     * 登记表
     * */
    private Map<CloudEventHandler, CloudEventObserverEntity> observerMap = new HashMap<>();

    /**
     * 登录关注
     * */
    public void add(EventLevel level, String queue, String topic, CloudEventHandler observer) {
        observerMap.put(observer, new CloudEventObserverEntity(level, queue, topic, observer));
    }

    /**
     * 向服务端推送关注
     */
    public void push() throws Exception {
        Set<String> instanceSubs = new LinkedHashSet<>();
        Set<String> clusterSubs = new LinkedHashSet<>();

        for (Map.Entry<CloudEventHandler, CloudEventObserverEntity> kv : observerMap.entrySet()) {
            CloudEventObserverEntity entity = kv.getValue();
            if (entity.level == EventLevel.cluster) {
                clusterSubs.add(entity.topic);
            } else {
                instanceSubs.add(entity.queue);
            }
        }


        String seal = WaterProps.instance.getEventSeal();

        Instance instance = Instance.local();

        //subscribeTopic(String subscriber_key, String subscriber_note, String receive_url, String access_key, String alarm_mobile, int receive_way, boolean is_unstable, String... topics)

        if (instanceSubs.size() > 0) {
            String instance_receiver_url = "http://" + instance.address + WW.path_msg_receiver;
            String instance_subscriber_Key = EncryptUtils.md5(instance.service + "_instance_" + instance_receiver_url);

            WaterClient.Message.subscribeTopic(instance_subscriber_Key,
                    instance.service,
                    instance_receiver_url,
                    seal,
                    "",
                    1,
                    Solon.cfg().isDriftMode(),
                    String.join(",", instanceSubs));
        }

        if (clusterSubs.size() > 0) {
            String cluster_hostname = WaterProps.instance.getEventHostname();
            if (Utils.isEmpty(cluster_hostname)) {
                cluster_hostname = instance.address;
            }

            String cluster_receiver_url = "http://" + cluster_hostname + WW.path_msg_receiver;
            String cluster_subscriber_Key = EncryptUtils.md5(instance.service + "_cluster_" + cluster_receiver_url);

            WaterClient.Message.subscribeTopic(cluster_subscriber_Key,
                    instance.service,
                    cluster_receiver_url,
                    seal,
                    "",
                    1,
                    false,
                    String.join(",", clusterSubs));
        }
    }
}
