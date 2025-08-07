/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test1;


import org.junit.jupiter.api.Test;
import org.noear.solon.statemachine.StateMachine;
import org.noear.solon.statemachine.StateTransition;
import test1.enums.OrderEvent;
import test1.enums.OrderState;

public class App {
    @Test
    public void test() {
        // 创建状态机实例
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>(OrderState.NONE);

        // 无 -> 创建订单
        stateMachine.addTransition(new StateTransition<>(OrderState.NONE, OrderState.CREATED, OrderEvent.CREATE,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已创建");
                    System.out.println(payload);
                }
        ));

        // 创建 -> 支付
        stateMachine.addTransition(new StateTransition<>(OrderState.CREATED, OrderState.PAID, OrderEvent.PAY,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已支付");
                    System.out.println(payload);
                }
        ));

        // 支付 -> 发货
        stateMachine.addTransition(new StateTransition<>(OrderState.PAID, OrderState.SHIPPED, OrderEvent.SHIP,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已发货");
                    System.out.println(payload);
                }
        ));


        // 发货 -> 送达
        stateMachine.addTransition(new StateTransition<>(OrderState.SHIPPED, OrderState.DELIVERED, OrderEvent.DELIVER,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已送达");
                    System.out.println(payload);
                }
        ));

        Order order = new Order("1", "iphone16 pro max", null);

        stateMachine.execute(OrderEvent.CREATE, order);
        stateMachine.execute(OrderEvent.PAY, order);
        stateMachine.execute(OrderEvent.SHIP, order);
        stateMachine.execute(OrderEvent.DELIVER, order);
    }
}