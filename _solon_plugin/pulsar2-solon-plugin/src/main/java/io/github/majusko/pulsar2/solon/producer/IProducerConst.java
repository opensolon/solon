package io.github.majusko.pulsar2.solon.producer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.pulsar.client.api.Producer;

interface IProducerConst {

	Map<String, Producer> producers = new ConcurrentHashMap<>();

}
