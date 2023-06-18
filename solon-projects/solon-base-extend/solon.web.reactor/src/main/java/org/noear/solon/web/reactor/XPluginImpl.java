package org.noear.solon.web.reactor;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author noear
 * @since 2.3
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        ActionReturnReactiveHandler reactiveHandler = new ActionReturnReactiveHandler();
        Bridge.actionReturnHandlers.put(Flux.class, reactiveHandler);
        Bridge.actionReturnHandlers.put(Mono.class, reactiveHandler);
    }
}
