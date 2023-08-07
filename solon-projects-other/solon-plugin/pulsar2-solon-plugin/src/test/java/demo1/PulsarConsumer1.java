package demo1;

import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar2.solon.annotation.PulsarConsumer;
import io.github.majusko.pulsar2.solon.constant.Serialization;
import org.junit.jupiter.api.Assertions;
import org.noear.solon.annotation.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class PulsarConsumer1 {
    public AtomicBoolean mockTopicListenerReceived = new AtomicBoolean(false);
    @PulsarConsumer(topic = "topic-one", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void topicOneListener(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        System.out.println("来到消费者....");
        System.out.println(myMsg.getData());
        mockTopicListenerReceived.set(true);
    }
}
