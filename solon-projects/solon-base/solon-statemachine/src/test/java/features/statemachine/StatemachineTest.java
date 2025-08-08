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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.statemachine.EventContext;
import org.noear.solon.statemachine.StateMachine;

public class StatemachineTest {
    /**
     * 正常流程测试
     */
    @Test
    public void test() {
        // 创建状态机实例
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();

        // 无 -> 创建订单
        stateMachine.addTransition(t ->
                t.from(OrderState.NONE).to(OrderState.CREATED).on(OrderEvent.CREATE).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已创建");
                    payload.setState(OrderState.CREATED);
                    System.out.println(payload);
                }));

        // 创建 -> 支付
        stateMachine.addTransition(t ->
                t.from(OrderState.CREATED).to(OrderState.PAID).on(OrderEvent.PAY).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已支付");
                    payload.setState(OrderState.PAID);
                    System.out.println(payload);
                }));

        // 支付 -> 发货
        stateMachine.addTransition(t ->
                t.from(OrderState.PAID).to(OrderState.SHIPPED).on(OrderEvent.SHIP).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已发货");
                    payload.setState(OrderState.SHIPPED);
                    System.out.println(payload);
                }));


        // 发货 -> 送达
        stateMachine.addTransition(t ->
                t.from(OrderState.SHIPPED).to(OrderState.DELIVERED).on(OrderEvent.DELIVER).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已送达");
                    payload.setState(OrderState.DELIVERED);
                    System.out.println(payload);
                }));

        Order order = new Order("1", "iphone16 pro max", null, OrderState.NONE);
        Order order2 = new Order("2", "iphone12 pro max", null, OrderState.NONE);

        stateMachine.sendEvent(OrderEvent.CREATE, order); //使用接口定制上下文
        stateMachine.sendEvent(OrderEvent.PAY, order);
        stateMachine.sendEvent(OrderEvent.SHIP, order);
        stateMachine.sendEvent(OrderEvent.DELIVER, order);

        stateMachine.sendEvent(OrderEvent.CREATE, EventContext.of(order2.getState(), order2)); //使用默认上下文
        stateMachine.sendEvent(OrderEvent.PAY, EventContext.of(order2.getState(), order2));
        stateMachine.sendEvent(OrderEvent.SHIP, EventContext.of(order2.getState(), order2));
        stateMachine.sendEvent(OrderEvent.DELIVER, EventContext.of(order2.getState(), order2));
    }

    /**
     * 取消功能测试
     */
    @Test
    public void cancelTest() {
        // 创建状态机实例
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();

        // 无 -> 创建订单
        stateMachine.addTransition(t ->
                t.from(OrderState.NONE).to(OrderState.CREATED).on(OrderEvent.CREATE).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已创建");
                    payload.setState(OrderState.CREATED);
                    System.out.println(payload);
                }));

        // 创建 -> 支付
        stateMachine.addTransition(t ->
                t.from(OrderState.CREATED).to(OrderState.PAID).on(OrderEvent.PAY).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已支付");
                    payload.setState(OrderState.PAID);
                    System.out.println(payload);
                }));

        // 创建 -> 取消
        stateMachine.addTransition(t ->
                t.from(OrderState.CREATED).to(OrderState.CANCELLED).on(OrderEvent.CANCEL).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已取消");
                    payload.setState(OrderState.CANCELLED);
                    System.out.println(payload);
                }));

        // 支付 -> 取消
        stateMachine.addTransition(t ->
                t.from(OrderState.PAID).to(OrderState.CANCELLED).on(OrderEvent.CANCEL).then(ctx -> {
                            Order payload = ctx.getPayload();
                            payload.setStatus("已取消（已退款）");
                            payload.setState(OrderState.CANCELLED);
                            System.out.println(payload);
                        }
                ));

        Order order1 = new Order("1", "iphone16 pro max", null, OrderState.NONE);
        Order order2 = new Order("2", "iphone12 pro max", null, OrderState.NONE);

        // 测试创建后取消
        stateMachine.sendEvent(OrderEvent.CREATE, order1); //使用接口定制上下文
        stateMachine.sendEvent(OrderEvent.CANCEL, order1);

        // 测试支付后取消
        stateMachine.sendEvent(OrderEvent.CREATE, EventContext.of(order2.getState(), order2)); //使用默认上下文
        stateMachine.sendEvent(OrderEvent.PAY, EventContext.of(order2.getState(), order2));
        stateMachine.sendEvent(OrderEvent.CANCEL, EventContext.of(order2.getState(), order2));
    }

    /**
     * 测试未按照状态配置异常执行
     */
    @Test
    public void invalidTransitionTest() {
        // 创建状态机实例
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();

        // 无 -> 创建订单
        stateMachine.addTransition(t ->
                t.from(OrderState.NONE).to(OrderState.CREATED).on(OrderEvent.CREATE).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已创建");
                    payload.setState(OrderState.CREATED);
                    System.out.println(payload);
                }));

        // 创建 -> 支付
        stateMachine.addTransition(t ->
                t.from(OrderState.CREATED).to(OrderState.PAID).on(OrderEvent.PAY).then(ctx -> {
                            Order payload = ctx.getPayload();
                            payload.setStatus("已支付");
                            payload.setState(OrderState.PAID);
                            System.out.println(payload);
                        }
                ));

        Order order = new Order("1", "iphone16 pro max", null, OrderState.NONE);

        // 测试无效转换：未创建订单就尝试支付
        try {
            stateMachine.sendEvent(OrderEvent.PAY, order);
        } catch (Exception e) {
            System.out.println("测试成功：捕获到异常 - " + e.getMessage());
        }

        // 创建订单后尝试重复创建
        stateMachine.sendEvent(OrderEvent.CREATE, order);
        try {
            stateMachine.sendEvent(OrderEvent.CREATE, order);
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
        stateMachine.addTransition(t ->
                t.from(OrderState.NONE).to(OrderState.CREATED).on(OrderEvent.CREATE).then(ctx -> {
                            Order payload = ctx.getPayload();
                            payload.setStatus("已创建");
                            payload.setState(OrderState.CREATED);
                            System.out.println(payload);
                        }
                ));

        // 创建 -> 支付
        stateMachine.addTransition(t ->
                t.from(OrderState.CREATED).to(OrderState.PAID).on(OrderEvent.PAY).then(ctx -> {
                            Order payload = ctx.getPayload();
                            payload.setStatus("已支付");
                            payload.setState(OrderState.PAID);
                            System.out.println("支付订单: " + payload);
                        }
                ));

        Order order = new Order("1", "iphone16 pro max", null, OrderState.NONE);

        // 创建订单
        stateMachine.sendEvent( OrderEvent.CREATE, order);

        // 编辑订单信息
        System.out.println("编辑前订单: " + order);
        order.setProductName("iphone16 pro max 256G");
        System.out.println("编辑后订单: " + order);

        // 继续支付流程
        stateMachine.sendEvent( OrderEvent.PAY, order);

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

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            stateMachine.addTransition(t ->
                    t.from(null).to(OrderState.CREATED).on(OrderEvent.CREATE).then(ctx -> {
                                Order payload = ctx.getPayload();
                                payload.setStatus("已创建");
                                payload.setState(OrderState.CREATED);
                                System.out.println(payload);
                            }
                    ));
        });
    }

    /**
     * 普通空参
     */
    @Test
    public void dslNullFieldTest() {
        StateMachine<OrderState, OrderEvent, Order> stateMachine = new StateMachine<>();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            stateMachine.addTransition(t ->
                    t.from(OrderState.NONE).to(null).on(OrderEvent.CREATE).then(ctx -> {
                        Order payload = ctx.getPayload();
                        payload.setStatus("已创建");
                        payload.setState(OrderState.CREATED);
                        System.out.println(payload);
                    }));
        });
    }
}