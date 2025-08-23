package demo.statemachine.case2;

import org.noear.solon.annotation.Managed;
import org.noear.solon.statemachine.StateMachine;

/**
 *
 * @author noear 2025/8/23 created
 *
 */
@Managed
public class OrderStateMachine extends StateMachine<OrderStatusEnum,OrderStatusEventEnum, OrderEntity> {
    public OrderStateMachine() {
        // 待支付 -> 已支付待发货（支付成功）
        addTransition(t -> t.
                from(OrderStatusEnum.WAIT_PAY)
                .to(OrderStatusEnum.WAIT_DELIVER)
                .on(OrderStatusEventEnum.PAY));

        // 已支付待发货 -> 已发货待收货（仓库已发货）
        addTransition(t -> t.
                from(OrderStatusEnum.WAIT_DELIVER)
                .to(OrderStatusEnum.WAIT_RECEIVE)
                .on(OrderStatusEventEnum.DELIVER));

        // 已发货待收货 -> 已收货（用户收货）
        addTransition(t -> t.
                from(OrderStatusEnum.WAIT_RECEIVE)
                .to(OrderStatusEnum.RECEIVED)
                .on(OrderStatusEventEnum.RECEIVE));
    }
}
