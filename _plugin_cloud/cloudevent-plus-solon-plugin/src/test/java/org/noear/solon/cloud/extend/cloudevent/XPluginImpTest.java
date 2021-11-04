package org.noear.solon.cloud.extend.cloudevent;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;

import java.util.Random;

class XPluginImpTest {

    public static class OrderPayEvent implements CloudEventEntity {}

    @Component
    public static class OrderListener {

        private final Random random = new Random();

        @CloudEventSubscriber
        public void onOrderPay(OrderPayEvent event) {
            if (random.nextBoolean()) {
                event.setSuccess(false);
            }
        }

    }

    @Component
    public static class OrderPayListener implements CloudEventHandler<OrderPayEvent> {

        @Override
        public void handle(OrderPayEvent event) {

        }

    }

    @Test
    void start() {
        Solon.start(PluginImp.class, new String[0]);
    }

}