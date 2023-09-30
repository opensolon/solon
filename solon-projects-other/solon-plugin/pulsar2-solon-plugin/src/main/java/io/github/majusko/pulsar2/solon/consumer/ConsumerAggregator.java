package io.github.majusko.pulsar2.solon.consumer;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.pulsar.client.api.BatchReceivePolicy;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.ConsumerInterceptor;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;

import io.github.majusko.pulsar2.solon.PulsarMessage;
import io.github.majusko.pulsar2.solon.annotation.SubscriptionProp;
import io.github.majusko.pulsar2.solon.collector.ConsumerHolder;
import io.github.majusko.pulsar2.solon.collector.IConsumerConst;
import io.github.majusko.pulsar2.solon.constant.BatchAckMode;
import io.github.majusko.pulsar2.solon.error.FailedBatchMessages;
import io.github.majusko.pulsar2.solon.error.FailedMessage;
import io.github.majusko.pulsar2.solon.error.exception.ClientInitException;
import io.github.majusko.pulsar2.solon.error.exception.ConsumerInitException;
import io.github.majusko.pulsar2.solon.properties.ConsumerProperties;
import io.github.majusko.pulsar2.solon.properties.PulsarProperties;
import io.github.majusko.pulsar2.solon.utils.SchemaUtils;
import io.github.majusko.pulsar2.solon.utils.UrlBuildService;
import org.noear.solon.core.AppContext;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

public class ConsumerAggregator {

    private final Sinks.Many<FailedMessage> sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
//    private final ConsumerCollector consumerCollector;
    private PulsarClient pulsarClient;
    private ConsumerProperties consumerProperties;
    private PulsarProperties pulsarProperties;
    private UrlBuildService urlBuildService;
    private ConsumerInterceptor consumerInterceptor;

    private List<Consumer> consumers;
    
    private List<CompletableFuture<?>> batchListenerList;
    

//    public ConsumerAggregator(PulsarClient pulsarClient,
//                              ConsumerProperties consumerProperties, PulsarProperties pulsarProperties, 
//                              ConsumerInterceptor consumerInterceptor) {
////        this.consumerCollector = consumerCollector;
//        this.pulsarClient = pulsarClient;
//        this.consumerProperties = consumerProperties;
//        this.pulsarProperties = pulsarProperties;
//        this.urlBuildService = new UrlBuildService(pulsarProperties,consumerProperties);
//        this.consumerInterceptor = consumerInterceptor;
//        this.batchListenerList = new ArrayList<>();
//    }
    
    public ConsumerAggregator() {
    	
    }


    public void init(AppContext context, ConsumerProperties acp) {
    	if(pulsarClient==null) {
            pulsarClient = context.getBean(PulsarClient.class);
    	}
    	if(consumerInterceptor==null) {
    		consumerInterceptor = context.getBean(ConsumerInterceptor.class);
    	}
    	if(pulsarProperties==null) {
    		pulsarProperties = context.getBean(PulsarProperties.class);
    	}
    	if(urlBuildService==null) {
//    		consumerProperties = acontext.getBean(ConsumerProperties.class);
    		consumerProperties = acp;
    		urlBuildService = new UrlBuildService(pulsarProperties,consumerProperties);
    	}
        if (pulsarProperties.isAutoStart()) {
        	consumers = new ArrayList<>();
        	for (Map.Entry<String, ConsumerHolder> $ : IConsumerConst.consumers.entrySet()) {
        		ConsumerHolder ch = $.getValue();
        		if(ch.getAnnotation().autoStart()) {
        			consumers.add(subscribe($.getKey(), $.getValue()));
        		}
        	}
        }
    }

    private Consumer<?> subscribe(String generatedConsumerName, ConsumerHolder holder) {
        try {
            final String consumerName = holder.getAnnotation().consumerName();
            final String subscriptionName = holder.getAnnotation().subscriptionName();
            final String topicName = holder.getAnnotation().topic();
            final String namespace = holder.getAnnotation().namespace();
            final SubscriptionType subscriptionType = urlBuildService.getSubscriptionType(holder);
            
            final ConsumerBuilder<?> consumerBuilder = pulsarClient
                    .newConsumer(SchemaUtils.getSchema(holder.getAnnotation().serialization(),
                            holder.getAnnotation().clazz()))
                    .consumerName(urlBuildService.buildPulsarConsumerName(consumerName, generatedConsumerName))
                    .subscriptionName(urlBuildService.buildPulsarSubscriptionName(subscriptionName, generatedConsumerName))
                    .topic(urlBuildService.buildTopicUrl(topicName, namespace))
                    .subscriptionType(subscriptionType)
                    .subscriptionInitialPosition(holder.getAnnotation().initialPosition());
            if (!holder.getAnnotation().batch()) {
                consumerBuilder.messageListener((consumer, msg) -> {
                    try {
                        final Method method = holder.getHandler();
                        method.setAccessible(true);
//                        Stopwatch stopwatch = Stopwatch.createStarted();

                        if (holder.isWrapped()) {
                            method.invoke(holder.getBean(), wrapMessage(msg));
                        } else {
                            method.invoke(holder.getBean(), msg.getValue());
                        }
                        consumer.acknowledge(msg);
                    } catch (Exception e) {
                        consumer.negativeAcknowledge(msg);
                        sink.tryEmitNext(new FailedMessage(e, consumer, msg));
                    }
                });
            }

            if(holder.getAnnotation().subscriptionProperties().length > 0) {
                consumerBuilder.subscriptionProperties(Arrays.stream(holder.getAnnotation().subscriptionProperties())
                    .collect(Collectors.toMap(SubscriptionProp::key, SubscriptionProp::value)));
            }

            if (pulsarProperties.isAllowInterceptor()) {
                consumerBuilder.intercept(consumerInterceptor);
            }

            if (consumerProperties.getAckTimeoutMs() > 0) {
                consumerBuilder.ackTimeout(consumerProperties.getAckTimeoutMs(), TimeUnit.MILLISECONDS);
            }

            if (holder.getAnnotation().batch()) {
                consumerBuilder.batchReceivePolicy(
                        BatchReceivePolicy.builder().maxNumMessages(holder.getAnnotation().maxNumMessage())
                                .maxNumBytes(holder.getAnnotation().maxNumBytes())
                                .timeout(holder.getAnnotation().timeoutMillis(), TimeUnit.MILLISECONDS).build());
            }

            urlBuildService.buildDeadLetterPolicy(holder.getAnnotation().maxRedeliverCount(),
                    holder.getAnnotation().deadLetterTopic(), consumerBuilder);

            final Consumer<?> consumer = consumerBuilder.subscribe();
            if (holder.getAnnotation().batch()) {
                createBatchListener(holder, consumer);
            }
            return consumer;
        } catch (PulsarClientException | ClientInitException e) {
            throw new ConsumerInitException("Failed to init consumer.", e);
        }
    }

	private void createBatchListener(ConsumerHolder holder, final Consumer<?> consumer) {
		CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
			boolean retTypeVoid = true;
			boolean manualAckMode = false;
			Messages<?> msgs = null;
			try {
				final Method method = holder.getHandler();
				method.setAccessible(true);
				retTypeVoid = method.getReturnType().equals(Void.TYPE);
				if (holder.getAnnotation().batchAckMode() == BatchAckMode.MANUAL) {
					manualAckMode = true;
				}
				while (true) {
					msgs = consumer.batchReceive();
					if (manualAckMode) {
						method.invoke(holder.getBean(), msgs, consumer);
					} else if (!retTypeVoid && !manualAckMode) {
						final List<MessageId> ackList = (List<MessageId>) method.invoke(holder.getBean(), msgs);
						final Set<MessageId> ackSet = ackList.stream().collect(Collectors.toSet());
						consumer.acknowledge(ackList);
						msgs.forEach((msg) -> {
							if (!ackSet.contains(msg))
								consumer.negativeAcknowledge(msg);
						});
					} else if (!manualAckMode) {
						method.invoke(holder.getBean(), msgs);
						consumer.acknowledge(msgs);
					}
				}
			} catch (Exception e) {
				if (retTypeVoid && !manualAckMode && msgs != null) {
					consumer.negativeAcknowledge(msgs);
				}
				sink.tryEmitNext(new FailedBatchMessages(e, consumer, msgs));
			}
		});
		batchListenerList.add(cf);
	}

    public <T> PulsarMessage<T> wrapMessage(Message<T> message) {
        final PulsarMessage<T> pulsarMessage = new PulsarMessage<T>();

        pulsarMessage.setValue(message.getValue());
        pulsarMessage.setMessageId(message.getMessageId());
        pulsarMessage.setSequenceId(message.getSequenceId());
        pulsarMessage.setProperties(message.getProperties());
        pulsarMessage.setTopicName(message.getTopicName());
        pulsarMessage.setKey(message.getKey());
        pulsarMessage.setEventTime(message.getEventTime());
        pulsarMessage.setPublishTime(message.getPublishTime());
        pulsarMessage.setProducerName(message.getProducerName());

        return pulsarMessage;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }
    
    public void setConsumers(List<Consumer> consumers) {
		this.consumers = consumers;
	}


	public List<CompletableFuture<?>> getBatchListenerList() {
		return batchListenerList;
	}

	public Disposable onError(java.util.function.Consumer<? super FailedMessage> consumer) {
        return sink.asFlux().subscribe(consumer);
    }

}
