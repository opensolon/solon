package demo.statemachine.case1;

import org.noear.solon.statemachine.EventContext;

public class Order implements EventContext<OrderStatus, Order> {
    private final String orderId;
    private OrderStatus status;

    public Order(String orderId, OrderStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    // for EventContext

    @Override
    public OrderStatus getCurrentState() {
        return status;
    }

    @Override
    public Order getPayload() {
        return this;
    }
}
