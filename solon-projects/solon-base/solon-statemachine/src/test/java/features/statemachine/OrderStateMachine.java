package features.statemachine;

import features.statemachine.enums.OrderEvent;
import features.statemachine.enums.OrderState;
import org.noear.solon.annotation.Component;
import org.noear.solon.statemachine.StateMachine;
import org.noear.solon.statemachine.StateTransition;

/**
 *
 * @author noear 2025/8/8 created
 *
 */
@Component
public class OrderStateMachine extends StateMachine<OrderState, OrderEvent, Order> {
    public OrderStateMachine() {
        // 无 -> 创建订单
        this.addTransition(new StateTransition<>(OrderState.NONE, OrderState.CREATED, OrderEvent.CREATE,
                (ctx) -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已创建");
                    payload.setState(OrderState.CREATED);
                    System.out.println(payload);
                }
        ));

        // 创建 -> 支付
        this.addTransition(new StateTransition<>(OrderState.CREATED, OrderState.PAID, OrderEvent.PAY,
                (ctx) -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已支付");
                    payload.setState(OrderState.PAID);
                    System.out.println(payload);
                }
        ));

        // 支付 -> 发货
        this.addTransition(new StateTransition<>(OrderState.PAID, OrderState.SHIPPED, OrderEvent.SHIP,
                (ctx) -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已发货");
                    payload.setState(OrderState.SHIPPED);
                    System.out.println(payload);
                }
        ));


        // 发货 -> 送达
        this.addTransition(new StateTransition<>(OrderState.SHIPPED, OrderState.DELIVERED, OrderEvent.DELIVER,
                (ctx) -> {
                    Order payload = ctx.getPayload();
                    payload.setStatus("已送达");
                    payload.setState(OrderState.DELIVERED);
                    System.out.println(payload);
                }
        ));
    }
}
