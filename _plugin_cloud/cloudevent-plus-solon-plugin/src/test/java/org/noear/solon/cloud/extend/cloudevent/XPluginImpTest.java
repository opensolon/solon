package org.noear.solon.cloud.extend.cloudevent;

import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;

import java.util.Random;

class XPluginImpTest {

    public static class OrderPayEvent implements CloudEventEntity {}

    @Component
    public static class OrderListener {

        private final Random random = new Random();

        @Subscribe
        public void onOrderPay(OrderPayEvent event) {
            if (random.nextBoolean()) {
                event.setSuccess(false);
            }
        }

    }

    @Component
    public static class OrderPayListener implements CloudEventSubscriber<OrderPayEvent> {

        @Override
        public void handle(OrderPayEvent event) {

        }

    }

    @Test
    void start() {
        Solon.start(XPluginImp.class, new String[0]);
    }

}