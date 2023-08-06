package demo1;


import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar2.solon.producer.ProducerFactory;
import io.github.majusko.pulsar2.solon.producer.PulsarTemplate;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2022/2/24 created
 */
//@Import(scanPackages = {"io.github.majusko.pulsar2.solon.producer","demo1"})
//@Import(value = {TestProducerConfiguration.class,Pulsar2ProducerConfiguration.class})
//@Import(value = {TestProducerConfiguration.class})
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(DemoApp.class)
public class DemoAppTests1 {
//    @Inject
//    private PulsarProducer1 pulsarProducer1;

    @Inject
    private ProducerFactory producerFactory;
    @Inject
    private PulsarTemplate pulsarTemplate;

    @Inject
    private PulsarConsumer1 pulsarConsumer1;
    @Test
    public  void test(){
//    	producerFactory.addProducer("topic-one", MyMsg.class);
    	System.out.println(producerFactory.getTopics());
        MyMsg mm = new MyMsg();
        mm.setData("hello pulsar2....");
        try {
            pulsarTemplate.send("topic-one",mm);
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        pulsarConsumer1.topicOneListener(mm);
    }
}
