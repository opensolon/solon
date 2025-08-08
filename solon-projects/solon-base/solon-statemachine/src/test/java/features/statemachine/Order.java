package features.statemachine;

import features.statemachine.enums.OrderState;
import org.noear.solon.statemachine.EventContext;

public class Order implements EventContext<OrderState, Order> {
    private String id;
    private String productName;
    private String status;
    private OrderState state;

    public Order(String id, String productName, String status, OrderState orderState) {
        this.id = id;
        this.productName = productName;
        this.status = status;
        this.state = orderState;
    }

    public Order() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    @Override
    public OrderState getCurrentState() {
        return getState();
    }

    @Override
    public Order getPayload() {
        return this;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", productName='" + productName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
