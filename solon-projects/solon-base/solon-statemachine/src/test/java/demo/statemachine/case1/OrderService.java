package demo.statemachine.case1;

import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Managed;

@Managed
public class OrderService {
    @Inject
    private OrderStateMachine orderStateMachine;

    //支付
    public Order pay() {
        Order order = new Order("1", OrderStatus.WAITING_RECEIVE);

        System.out.println("尝试支付，订单号：" + order.getOrderId());

        try {
            //事件： 支付, 状态转换：待支付 → 待发货
            orderStateMachine.sendEvent(OrderEvent.PAY_ORDER, order);

            System.out.println("支付成功，订单号：" + order.getOrderId());
        } catch (Exception ex) {
            System.out.println("支付失败, 状态异常，订单号：" + order.getOrderId());
        }

        return order;
    }
}