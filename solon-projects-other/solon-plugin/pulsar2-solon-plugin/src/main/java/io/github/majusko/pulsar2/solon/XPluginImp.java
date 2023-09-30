package io.github.majusko.pulsar2.solon;

import org.apache.pulsar.client.api.PulsarClient;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.LogUtil;

import io.github.majusko.pulsar2.solon.annotation.PulsarConsumer;
import io.github.majusko.pulsar2.solon.collector.ConsumerCollector;
import io.github.majusko.pulsar2.solon.consumer.ConsumerAggregator;
import io.github.majusko.pulsar2.solon.producer.ProducerCollector;
import io.github.majusko.pulsar2.solon.producer.PulsarProducerFactory;
import io.github.majusko.pulsar2.solon.producer.PulsarTemplate;
import io.github.majusko.pulsar2.solon.properties.ConsumerProperties;
import io.github.majusko.pulsar2.solon.properties.PulsarProperties;

public class XPluginImp implements Plugin {

	@Override
	public void start(AppContext context) throws Throwable {
		EnablePulsar2 annoEp2 =Solon.app().source().getAnnotation(EnablePulsar2.class);
		if (annoEp2 == null) {
			return;
		}

		context.beanMake(PulsarProperties.class);
		BeanWrap cp = context.beanMake(ConsumerProperties.class);

		context.beanMake(Pulsar2AutoConfiguration.class);
		context.beanMake(Pulsar2ProducerConfiguration.class);
		context.beanMake(Pulsar2ConsumerConfiguration.class);
		context.beanMake(Pulsar2FluxAutoConfiguration.class);

		ConsumerCollector consumerBeanBuilder = new ConsumerCollector(context);
		context.beanExtractorAdd(PulsarConsumer.class, consumerBeanBuilder);

		context.wrapAndPut(PulsarTemplate.class, new PulsarTemplate());
		// 必然入库
//		context.putWrap(PulsarTemplate.class, bw);

		ProducerCollector producerBeanBuilder = new ProducerCollector();

		// 晚点启动，让扫描时产生的组件可以注册进来
		EventBus.subscribe(AppLoadEndEvent.class, e -> {
			AppContext acontext = e.context();

			acontext.subWrapsOfType(PulsarProducerFactory.class, ppf -> {
				producerBeanBuilder.doBuild(ppf, acontext);
			});
		});
		
		if(annoEp2.consumerAggregator()) {
			ConsumerAggregator ca = new ConsumerAggregator();
			context.subWrapsOfType(PulsarClient.class, pbw -> {
				ca.init(context,cp.raw());
				// 必然入库
				context.wrapAndPut(ConsumerAggregator.class, ca);
			});
		}
	}

//	@Override
//	public void prestop() throws Throwable {
//		AppContext context = Solon.app().context();
//		LogUtil.global().info("[Solon] APP Bean Plugin Load End...");
//		ProducerCollector producerBeanBuilder = new ProducerCollector(context);
//		List<BeanWrap> bws = context.getWrapsOfType(PulsarProducerFactory.class);
//		if (bws != null && bws.size() > 0) {
//			for (BeanWrap b : bws) {
//				producerBeanBuilder.postProcessBeforeInitialization(b);
//			}
//		}
//	}

}
