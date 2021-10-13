package org.noear.solon.extend.redisx.plus;

import org.noear.solon.extend.redisx.RedisClient;

import java.util.function.Consumer;

/**
 * Redis 队列
 *
 * @author noear
 * @since 1.5
 */
public class RedisQueue {
    private final RedisClient client;
    private final String queueName;

    public RedisQueue(RedisClient client, String queueName) {
        this.client = client;
        this.queueName = queueName;
    }

    /**
     * 添加
     * */
    public void add(String item) {
        client.open0(session -> session.key(queueName).expire(-2).listAdd(item));
    }

    /**
     * 冒泡
     * */
    public String pop() {
        return client.open1(session -> session.key(queueName).listPop());
    }

    /**
     * 冒泡更多
     * */
    public void popAll(Consumer<String> consumer) {
        client.open0(session -> {
            session.key(queueName);

            while (true) {
                String item = session.listPop();
                if (item == null) {
                    break;
                } else {
                    consumer.accept(item);
                }
            }
        });
    }

    /**
     * 预览
     * */
    public String peek() {
        return client.open1(session -> session.key(queueName).listPeek());
    }
}
