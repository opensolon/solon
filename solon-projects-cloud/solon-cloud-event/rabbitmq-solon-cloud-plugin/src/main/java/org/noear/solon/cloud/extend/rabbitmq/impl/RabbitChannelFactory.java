package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.noear.solon.Utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 通道工厂
 *
 * @author noear
 * @since 1.2
 * @since 2.4
 */
public class RabbitChannelFactory {
    private ConnectionFactory connectionFactory;
    public RabbitChannelFactory(RabbitConfig config) {
        String host = config.server.split(":")[0];
        int port = Integer.parseInt(config.server.split(":")[1]);

        connectionFactory = new ConnectionFactory();

        // 配置连接信息
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setRequestedHeartbeat(30);

        if (Utils.isNotEmpty(config.username)) {
            connectionFactory.setUsername(config.username);
        }
        if (Utils.isNotEmpty(config.password)) {
            connectionFactory.setPassword(config.password);
        }
        if (Utils.isNotEmpty(config.virtualHost)) {
            connectionFactory.setVirtualHost(config.virtualHost);
        }

        // 网络异常自动连接恢复
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 每5秒尝试重试连接一次
        connectionFactory.setNetworkRecoveryInterval(5000L);
    }


    /**
     * 创建通道
     * */
    public Channel createChannel() throws IOException, TimeoutException {
        return connectionFactory.newConnection().createChannel();
    }
}
