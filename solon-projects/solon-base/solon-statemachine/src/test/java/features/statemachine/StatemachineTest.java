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
package features.statemachine;


import features.statemachine.enums.OrderEvent;
import features.statemachine.enums.OrderState;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.Assert;
import org.noear.solon.statemachine.StateMachine;
import org.noear.solon.statemachine.StateTransition;

public class StatemachineTest {
    /**
     * 正常流程测试
     */
    @Test
    public void test() {
        // 创建状态机实例
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();

        // 无 -> 创建订单
        stateMachine.addTransition(new StateTransition<>(OrderState.NONE, OrderState.CREATED, OrderEvent.CREATE,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已创建");
                    payload.setState(OrderState.CREATED);
                    System.out.println(payload);
                }
        ));

        // 创建 -> 支付
        stateMachine.addTransition(new StateTransition<>(OrderState.CREATED, OrderState.PAID, OrderEvent.PAY,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已支付");
                    payload.setState(OrderState.PAID);
                    System.out.println(payload);
                }
        ));

        // 支付 -> 发货
        stateMachine.addTransition(new StateTransition<>(OrderState.PAID, OrderState.SHIPPED, OrderEvent.SHIP,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已发货");
                    payload.setState(OrderState.SHIPPED);
                    System.out.println(payload);
                }
        ));


        // 发货 -> 送达
        stateMachine.addTransition(new StateTransition<>(OrderState.SHIPPED, OrderState.DELIVERED, OrderEvent.DELIVER,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已送达");
                    payload.setState(OrderState.DELIVERED);
                    System.out.println(payload);
                }
        ));

        Order order = new Order("1", "iphone16 pro max", null, OrderState.NONE);
        Order order2 = new Order("2", "iphone12 pro max", null, OrderState.NONE);

        stateMachine.execute(order.getState(), OrderEvent.CREATE, order);
        stateMachine.execute(order.getState(), OrderEvent.PAY, order);
        stateMachine.execute(order.getState(), OrderEvent.SHIP, order);
        stateMachine.execute(order.getState(), OrderEvent.DELIVER, order);

        stateMachine.execute(order2.getState(), OrderEvent.CREATE, order2);
        stateMachine.execute(order2.getState(), OrderEvent.PAY, order2);
        stateMachine.execute(order2.getState(), OrderEvent.SHIP, order2);
        stateMachine.execute(order2.getState(), OrderEvent.DELIVER, order2);

    }

    /**
     * 链式调用测试
     */
    @Test
    public void dslTest() {
        // 创建状态机实例
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();

        // 无 -> 创建订单
        stateMachine.addTransition(
                StateTransition.<OrderState, OrderEvent, Order>builder()
                        .from(OrderState.NONE)
                        .to(OrderState.CREATED)
                        .event(OrderEvent.CREATE)
                        .action((order) -> {
                            Order payload = order.getPayload();
                            payload.setStatus("已创建");
                            payload.setState(OrderState.CREATED);
                            System.out.println(payload);
                        })
                        .build()
        );

        // 创建 -> 支付
        stateMachine.addTransition(
                StateTransition.<OrderState, OrderEvent, Order>builder()
                        .from(OrderState.CREATED)
                        .to(OrderState.PAID)
                        .event(OrderEvent.PAY)
                        .action((order) -> {
                            Order payload = order.getPayload();
                            payload.setStatus("已支付");
                            payload.setState(OrderState.PAID);
                            System.out.println(payload);
                        })
                        .build()
        );

        // 支付 -> 发货
        stateMachine.addTransition(
                StateTransition.<OrderState, OrderEvent, Order>builder()
                        .from(OrderState.PAID)
                        .to(OrderState.SHIPPED)
                        .event(OrderEvent.SHIP)
                        .action((order) -> {
                            Order payload = order.getPayload();
                            payload.setStatus("已发货");
                            payload.setState(OrderState.SHIPPED);
                            System.out.println(payload);
                        })
                        .build()
        );


        // 发货 -> 送达
        stateMachine.addTransition(
                StateTransition.<OrderState, OrderEvent, Order>builder()
                        .from(OrderState.SHIPPED)
                        .to(OrderState.DELIVERED)
                        .event(OrderEvent.DELIVER)
                        .action((order) -> {
                            Order payload = order.getPayload();
                            payload.setStatus("已送达");
                            payload.setState(OrderState.DELIVERED);
                            System.out.println(payload);
                        })
                        .build()
        );

        Order order = new Order("1", "iphone16 pro max", null, OrderState.NONE);
        Order order2 = new Order("2", "iphone12 pro max", null, OrderState.NONE);

        stateMachine.execute(order.getState(), OrderEvent.CREATE, order);
        stateMachine.execute(order2.getState(), OrderEvent.CREATE, order2);
        stateMachine.execute(order2.getState(), OrderEvent.PAY, order2);
        stateMachine.execute(order.getState(), OrderEvent.PAY, order);
        stateMachine.execute(order2.getState(), OrderEvent.SHIP, order2);
        stateMachine.execute(order.getState(), OrderEvent.SHIP, order);
        stateMachine.execute(order.getState(), OrderEvent.DELIVER, order);

        stateMachine.execute(order2.getState(), OrderEvent.DELIVER, order2);

    }

    /**
     * 取消功能测试
     */
    @Test
    public void cancelTest() {
        // 创建状态机实例
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();

        // 无 -> 创建订单
        stateMachine.addTransition(new StateTransition<>(OrderState.NONE, OrderState.CREATED, OrderEvent.CREATE,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已创建");
                    payload.setState(OrderState.CREATED);
                    System.out.println(payload);
                }
        ));

        // 创建 -> 支付
        stateMachine.addTransition(new StateTransition<>(OrderState.CREATED, OrderState.PAID, OrderEvent.PAY,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已支付");
                    payload.setState(OrderState.PAID);
                    System.out.println(payload);
                }
        ));

        // 创建 -> 取消
        stateMachine.addTransition(new StateTransition<>(OrderState.CREATED, OrderState.CANCELLED, OrderEvent.CANCEL,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已取消");
                    payload.setState(OrderState.CANCELLED);
                    System.out.println(payload);
                }
        ));

        // 支付 -> 取消
        stateMachine.addTransition(new StateTransition<>(OrderState.PAID, OrderState.CANCELLED, OrderEvent.CANCEL,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已取消（已退款）");
                    payload.setState(OrderState.CANCELLED);
                    System.out.println(payload);
                }
        ));

        Order order1 = new Order("1", "iphone16 pro max", null, OrderState.NONE);
        Order order2 = new Order("2", "iphone12 pro max", null, OrderState.NONE);

        // 测试创建后取消
        stateMachine.execute(order1.getState(), OrderEvent.CREATE, order1);
        stateMachine.execute(order1.getState(), OrderEvent.CANCEL, order1);

        // 测试支付后取消
        stateMachine.execute(order2.getState(), OrderEvent.CREATE, order2);
        stateMachine.execute(order2.getState(), OrderEvent.PAY, order2);
        stateMachine.execute(order2.getState(), OrderEvent.CANCEL, order2);
    }

    /**
     * 测试未按照状态配置异常执行
     */
    @Test
    public void invalidTransitionTest() {
        // 创建状态机实例
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();

        // 无 -> 创建订单
        stateMachine.addTransition(new StateTransition<>(OrderState.NONE, OrderState.CREATED, OrderEvent.CREATE,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已创建");
                    payload.setState(OrderState.CREATED);
                    System.out.println(payload);
                }
        ));

        // 创建 -> 支付
        stateMachine.addTransition(new StateTransition<>(OrderState.CREATED, OrderState.PAID, OrderEvent.PAY,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已支付");
                    payload.setState(OrderState.PAID);
                    System.out.println(payload);
                }
        ));

        Order order = new Order("1", "iphone16 pro max", null, OrderState.NONE);

        // 测试无效转换：未创建订单就尝试支付
        try {
            stateMachine.execute(order.getState(), OrderEvent.PAY, order);
        } catch (Exception e) {
            System.out.println("测试成功：捕获到异常 - " + e.getMessage());
        }

        // 创建订单后尝试重复创建
        stateMachine.execute(order.getState(), OrderEvent.CREATE, order);
        try {
            stateMachine.execute(order.getState(), OrderEvent.CREATE, order);
            System.out.println("测试失败：应该抛出异常");
        } catch (Exception e) {
            System.out.println("测试成功：捕获到异常 - " + e.getMessage());
        }
    }

    /**
     * 测试修改订单
     */
    @Test
    public void editOrderTest() {
        // 创建状态机实例
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();

        // 无 -> 创建订单
        stateMachine.addTransition(new StateTransition<>(OrderState.NONE, OrderState.CREATED, OrderEvent.CREATE,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已创建");
                    payload.setState(OrderState.CREATED);
                    System.out.println(payload);
                }
        ));

        // 创建 -> 支付
        stateMachine.addTransition(new StateTransition<>(OrderState.CREATED, OrderState.PAID, OrderEvent.PAY,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已支付");
                    payload.setState(OrderState.PAID);
                    System.out.println("支付订单: " + payload);
                }
        ));

        Order order = new Order("1", "iphone16 pro max", null, OrderState.NONE);

        // 创建订单
        stateMachine.execute(order.getState(), OrderEvent.CREATE, order);

        // 编辑订单信息
        System.out.println("编辑前订单: " + order);
        order.setProductName("iphone16 pro max 256G");
        System.out.println("编辑后订单: " + order);

        // 继续支付流程
        stateMachine.execute(order.getState(), OrderEvent.PAY, order);

        // 再次编辑订单（已支付状态下）
        try {
            order.setProductName("iphone16 pro max 512G");
            System.out.println("已支付状态下编辑订单: " + order);
            // 验证订单状态是否受编辑影响
            System.out.println("订单状态: " + order.getState());
        } catch (Exception e) {
            System.out.println("编辑失败: " + e.getMessage());
        }
    }

    /**
     * 普通空参
     */
    @Test
    public void normalNullFieldTest() {
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();
        stateMachine.addTransition(new StateTransition<>(null, OrderState.CREATED, OrderEvent.CREATE,
                (order) -> {
                    Order payload = order.getPayload();
                    payload.setStatus("已创建");
                    payload.setState(OrderState.CREATED);
                    System.out.println(payload);
                }
        ));

    }

    /**
     * 普通空参
     */
    @Test
    public void dslNullFieldTest() {
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();
        stateMachine.addTransition(
                StateTransition.<OrderState, OrderEvent, Order>builder()
                        .from(OrderState.NONE)
                        .to(null)
                        .event(OrderEvent.CREATE)
                        .action((order) -> {
                            Order payload = order.getPayload();
                            payload.setStatus("已创建");
                            payload.setState(OrderState.CREATED);
                            System.out.println(payload);
                        })
                        .build()
        );

    }
}