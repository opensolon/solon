package io.github.majusko.pulsar2.solon.producer;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;

import io.github.majusko.pulsar2.solon.annotation.PulsarProducer;
import io.github.majusko.pulsar2.solon.collector.ProducerHolder;
import io.github.majusko.pulsar2.solon.error.exception.ProducerInitException;
import io.github.majusko.pulsar2.solon.properties.ConsumerProperties;
import io.github.majusko.pulsar2.solon.properties.PulsarProperties;
import io.github.majusko.pulsar2.solon.utils.SchemaUtils;
import io.github.majusko.pulsar2.solon.utils.UrlBuildService;

public class ProducerCollector{
    private PulsarClient pulsarClient;
    private UrlBuildService urlBuildService;
    private PulsarProperties pulsarProperties;

    private ProducerInterceptor producerInterceptor;

    private Producer<?> buildProducer(ProducerHolder holder) {
        try {
            final ProducerBuilder<?> producerBuilder = pulsarClient.newProducer(getSchema(holder))
                .compressionType(holder.getCompressionType())
                    .topic(holder.getNamespace()
                            .map(namespace -> urlBuildService.buildTopicUrl(holder.getTopic(), namespace))
                            .orElseGet(() -> urlBuildService.buildTopicUrl(holder.getTopic())));

            if(pulsarProperties.isAllowInterceptor()) {
                producerBuilder.intercept(producerInterceptor);
            }

            return producerBuilder.create();
        } catch (PulsarClientException e) {
            throw new ProducerInitException("Failed to init producer.", e);
        }
    }

    private <T> Schema<?> getSchema(ProducerHolder holder) throws RuntimeException {
        return SchemaUtils.getSchema(holder.getSerialization(), holder.getClazz());
    }
    
    public void doBuild(BeanWrap bean, AppContext context) {
    	Annotation[] annos = bean.annotations();
    	boolean hasPulsarProducer = false;
    	if(annos != null && annos.length > 0) {
    		for(Annotation a : annos) {
    			String name = a.annotationType().getSimpleName();
    			if(name.equals("PulsarProducer") && a instanceof PulsarProducer) {
    				hasPulsarProducer = true;
    				break;
    			}
    		}
    	}
    	Object raw = bean.raw();
        if (hasPulsarProducer && (raw instanceof PulsarProducerFactory)) {
        	if(pulsarClient==null) {
                pulsarClient = context.getBean(PulsarClient.class);
        	}
        	if(producerInterceptor==null) {
        		producerInterceptor = context.getBean(ProducerInterceptor.class);
        	}
        	if(pulsarProperties==null) {
        		pulsarProperties = context.getBean(PulsarProperties.class);
        	}
        	if(urlBuildService==null) {
        		ConsumerProperties consumerProperties = context.getBean(ConsumerProperties.class);
        		urlBuildService = new UrlBuildService(pulsarProperties,consumerProperties);
        	}
        	Map<String, ProducerMaker> topics = ((PulsarProducerFactory) raw).getTopics();
        	for (Map.Entry<String, ProducerMaker> $ : topics.entrySet()) {
        		ProducerMaker pm = $.getValue();
        		String n = pm.getNamespace().orElse(null);
        		ProducerHolder ph = null;
        		if(Utils.isNotBlank(n)) {
        			ph = new ProducerHolder(pm.getTopic(),pm.getClazz(),pm.getSerialization(),n,pm.getCompressionType());
        		}else {
        			ph = new ProducerHolder(pm.getTopic(),pm.getClazz(),pm.getSerialization(),pm.getCompressionType());
        		}
        		Producer<?> p =buildProducer(ph);
        		IProducerConst.producers.put(pm.getTopic(), p);
        	}
        }
    }
}
