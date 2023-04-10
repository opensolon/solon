package io.github.majusko.pulsar2.solon.collector;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface IConsumerConst {

    Set<Class<?>> nonAnnotatedClasses = new HashSet<>();
    
    Map<String, ConsumerHolder> consumers = new ConcurrentHashMap<>();

}
