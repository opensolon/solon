package features.statemachine;

import features.statemachine.enums.OrderEvent;
import features.statemachine.enums.OrderState;
import org.noear.solon.annotation.Managed;
import org.noear.solon.statemachine.StateMachine;

/**
 *
 * @author noear 2025/8/8 created
 * <pre>{@code
 * @Managed
 * public class Demo {
 *     @Inject
 *     OrderStateMachine orderStateMachine;
 *
 *     public void test(OrderEventContext order){
 *         orderStateMachine.sendEvent(OrderEvent.CREATE, order);
 *     }
 * }
 * }</pre>
 */
@Managed
public class OrderStateMachine extends StateMachine<OrderState, OrderEvent, Order> {
    public OrderStateMachine() {
        // 无 -> 创建订单
        from(OrderState.NONE).to(OrderState.CREATED).on(OrderEvent.CREATE).then(ctx -> {
            Order payload = ctx.getPayload();
            payload.setStatus("已创建");
            payload.setState(OrderState.CREATED);
            System.out.println(payload);
        });

        // 创建 -> 支付
        from(OrderState.CREATED).to(OrderState.PAID).on(OrderEvent.PAY).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已支付");
                    payload.setState(OrderState.PAID);
                    System.out.println(payload);
                }
        );

        // 支付 -> 发货
        from(OrderState.PAID).to(OrderState.SHIPPED).on(OrderEvent.SHIP).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已发货");
                    payload.setState(OrderState.SHIPPED);
                    System.out.println(payload);
                }
        );


        // 发货 -> 送达
        from(OrderState.SHIPPED).to(OrderState.DELIVERED).on(OrderEvent.DELIVER).then(ctx -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已送达");
                    payload.setState(OrderState.DELIVERED);
                    System.out.println(payload);
                }
        );
    }
}
