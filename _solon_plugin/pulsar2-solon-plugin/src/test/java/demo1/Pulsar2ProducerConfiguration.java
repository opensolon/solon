package demo1;

import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar2.solon.producer.ProducerFactory;
import io.github.majusko.pulsar2.solon.producer.PulsarProducerFactory;
import io.github.majusko.pulsar2.solon.producer.PulsarTemplate;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

//@Configuration
public class Pulsar2ProducerConfiguration {

//    @Bean
//    @Condition(onMissingBean = PulsarTemplate.class)
//    public PulsarTemplate getPulsarTemplate(){
//    	System.out.println("Pulsar2ProducerConfiguration PulsarTemplate");
//        return new PulsarTemplate();
//    }

//    @Bean
//    @Condition(onMissingBean = PulsarProducerFactory.class)
//    public PulsarProducerFactory gePulsarProducerFactory(){
//        return new ProducerFactory();
//    }

}
