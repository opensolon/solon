package demo.statemachine.case1;

import org.noear.solon.annotation.Managed;
import org.noear.solon.statemachine.StateMachine;

@Managed
public class OrderStateMachine extends StateMachine<OrderStatus, OrderEvent,Order> {
    public OrderStateMachine() {
        // 订单待支付 -> 待取货
        from(OrderStatus.WAITING_PAYMENT).to(OrderStatus.WAITING_RECEIVE)
                .on(OrderEvent.PAY_ORDER)
                .then(c -> c.getPayload().setStatus(c.getTo()));

        // 订单待取货 -> 已完成
        from(OrderStatus.WAITING_RECEIVE).to(OrderStatus.FINISHED)
                .on(OrderEvent.FINISH_ORDER)
                .then(c -> c.getPayload().setStatus(c.getTo()));

        // 订单待支付 -> 已取消
        from(OrderStatus.WAITING_PAYMENT).to(OrderStatus.CANCELED)
                .on(OrderEvent.CANCEL_ORDER)
                .then(c -> c.getPayload().setStatus(c.getTo()));
    }
}