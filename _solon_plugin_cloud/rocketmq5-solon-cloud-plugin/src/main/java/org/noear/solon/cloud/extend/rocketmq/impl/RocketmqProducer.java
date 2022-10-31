package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Event;

import java.time.Duration;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqProducer {
    RocketmqConfig cfg;
    ClientServiceProvider serviceProvider;
    Producer producer;

    public RocketmqProducer(RocketmqConfig config) {
        cfg = config;
    }

    private void init(CloudProps cloudProps) throws ClientException {
        if (producer != null) {
            return;
        }

        synchronized (this) {
            if (producer != null) {
                return;
            }


            serviceProvider = ClientServiceProvider.loadService();

            ClientConfigurationBuilder builder = ClientConfiguration.newBuilder();

            //服务地址
            builder.setEndpoints(cfg.getServer());

            //发送超时时间，默认3000 单位ms
            if (cfg.getTimeout() > 0) {
                builder.setRequestTimeout(Duration.ofMillis(cfg.getTimeout()));
            }

            ClientConfiguration configuration = builder.build();

            producer = serviceProvider.newProducerBuilder()
                    .setClientConfiguration(configuration)
                    .build();
        }
    }

    public boolean publish(CloudProps cloudProps, Event event, String topic) throws ClientException {
        init(cloudProps);

        //普通消息发送。
        Message message = MessageUtil.buildNewMeaage(serviceProvider, event, topic);

        //发送消息，需要关注发送结果，并捕获失败等异常。
        SendReceipt sendReceipt = producer.send(message);

        if (sendReceipt != null) {
            return true;
        } else {
            return false;
        }
    }
}
