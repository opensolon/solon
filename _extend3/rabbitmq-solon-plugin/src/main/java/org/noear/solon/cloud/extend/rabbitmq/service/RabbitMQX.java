package org.noear.solon.cloud.extend.rabbitmq.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.noear.solon.Utils;
import org.noear.solon.ext.ConsumerEx;

import java.io.IOException;

/**
 * @author noear
 * @since 1.2
 */
public class RabbitMQX {
    String host;
    int port;
    String user;
    String password;

    ConnectionFactory connectionFactory;


    public RabbitMQX(String server, String user, String password) {
        if (server.contains(":") == false) {
            throw new RuntimeException("RabbitX:Properties error the server parameter!");
        }

        this.host = server.split(":")[0];
        this.port = Integer.parseInt(server.split(":")[1]);
        this.user = user;
        this.password = password;

        connectionFactory = new ConnectionFactory();

        // 配置连接信息
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);

        if (Utils.isEmpty(user) == false) {
            connectionFactory.setUsername(user);
        }
        if (Utils.isEmpty(password) == false) {
            connectionFactory.setPassword(password);
        }

        // 网络异常自动连接恢复
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 每10秒尝试重试连接一次
        connectionFactory.setNetworkRecoveryInterval(10000);
    }

    public Channel createChannel() throws Exception {
        Connection connection = connectionFactory.newConnection();
        return connection.createChannel();
    }

    public void open0(ConsumerEx<Channel> action) {
        try {
            try (Connection connection = connectionFactory.newConnection()) {
                try (Channel channel = connection.createChannel()) {
                    action.accept(channel);
                }
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
