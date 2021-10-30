package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 通道工厂
 *
 * @author noear
 * @since 1.2
 */
public class RabbitChannelFactory {
    ConnectionFactory connectionFactory;
    RabbitConfig config;


    CloudProps cloudProps;
    public RabbitChannelFactory(CloudProps cloudProps, RabbitConfig cfg) {
        this.cloudProps = cloudProps;

        config = cfg;

        String host = config.server.split(":")[0];
        int port = Integer.parseInt(config.server.split(":")[1]);
        String virtualHost = getEventVirtualHost();

        connectionFactory = new ConnectionFactory();

        // 配置连接信息
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);

        if (Utils.isNotEmpty(config.username)) {
            connectionFactory.setUsername(config.username);
        }
        if (Utils.isNotEmpty(config.password)) {
            connectionFactory.setPassword(config.password);
        }
        if (Utils.isNotEmpty(virtualHost)) {
            connectionFactory.setVirtualHost(virtualHost);
        }

        // 网络异常自动连接恢复
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 每10秒尝试重试连接一次
        connectionFactory.setNetworkRecoveryInterval(10000);
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public RabbitConfig getConfig() {
        return config;
    }

    private Connection connection;
    public Connection getConnection() throws IOException, TimeoutException {
        if (connection == null) {
            connection = connectionFactory.newConnection();
        }

        return connection;
    }

    private Channel channel;
    public Channel getChannel() throws IOException, TimeoutException {
        if (channel == null) {
            channel = getConnection().createChannel();
        }

        return channel;
    }

    /**
     * 虚拟主机
     */
    public String getEventVirtualHost() {
        return cloudProps.getProp(RabbitmqProps.PROP_EVENT_virtualHost);
    }
}
