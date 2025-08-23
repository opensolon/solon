package demo.statemachine.case2;

/**
 *
 * @author noear 2025/8/23 created
 *
 */
public class OrderEntity {
    private final Integer id;
    private OrderStatusEnum status;

    public OrderEntity(Integer id, OrderStatusEnum status) {
        this.id = id;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }
}
