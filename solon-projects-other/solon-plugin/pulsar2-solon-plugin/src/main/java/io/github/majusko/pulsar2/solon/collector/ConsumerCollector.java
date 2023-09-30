package io.github.majusko.pulsar2.solon.collector;

import static io.github.majusko.pulsar2.solon.utils.SchemaUtils.getParameterType;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;

import io.github.majusko.pulsar2.solon.annotation.PulsarConsumer;
import io.github.majusko.pulsar2.solon.properties.ConsumerProperties;
import io.github.majusko.pulsar2.solon.properties.PulsarProperties;
import io.github.majusko.pulsar2.solon.utils.UrlBuildService;
import org.noear.solon.core.util.ReflectUtil;

public class ConsumerCollector implements BeanExtractor<PulsarConsumer> {

	private final UrlBuildService urlBuildService;

//	private final Map<String, ConsumerHolder> consumers = new ConcurrentHashMap<>();

	public ConsumerCollector(AppContext context) {
		PulsarProperties pp = context.getBean(PulsarProperties.class);
		ConsumerProperties cp = context.getBean(ConsumerProperties.class);
		this.urlBuildService = new UrlBuildService(pp, cp);
	}

	public Map<String, ConsumerHolder> getConsumers() {
		return IConsumerConst.consumers;
	}

	public Optional<ConsumerHolder> getConsumer(String methodDescriptor) {
		return Optional.ofNullable(IConsumerConst.consumers.get(methodDescriptor));
	}

	@Override
	public void doExtract(BeanWrap bw, Method method, PulsarConsumer anno) throws Throwable {
		final Class<?> beanClass = bw.clz();

		if (IConsumerConst.nonAnnotatedClasses.contains(beanClass)) {
			return;
		}
		Object bean = bw.raw();
		Method[] ms = ReflectUtil.getDeclaredMethods(beanClass);
		for(Method $:ms) {
			if (!$.isAnnotationPresent(PulsarConsumer.class)) {
				IConsumerConst.nonAnnotatedClasses.add(beanClass);
				continue;
			}
			String cn = urlBuildService.buildConsumerName(beanClass, $);
			ConsumerHolder ch = new ConsumerHolder($.getAnnotation(PulsarConsumer.class), $, bean,
					getParameterType($));
			IConsumerConst.consumers.put(cn, ch);
		}
	}

}
