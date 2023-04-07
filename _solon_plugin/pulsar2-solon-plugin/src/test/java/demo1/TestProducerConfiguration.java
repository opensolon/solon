package demo1;

import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar2.solon.producer.ProducerFactory;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

@Configuration
public class TestProducerConfiguration {

    @Bean
    public ProducerFactory producerFactory() {
    	System.out.println("TestProducerConfiguration ProducerFactory");
        return new ProducerFactory()
                .addProducer("my-topic", MyMsg.class)
                .addProducer("topic-one", MyMsg.class)
                .addProducer("other-topic", String.class);
    }
}
