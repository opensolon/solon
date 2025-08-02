package test1.enums;


import org.noear.solon.statemachine.State;

/**
 * 订单状态枚举
 */
public enum OrderState implements State {
    NONE,CREATED, PAID, SHIPPED, DELIVERED, CANCELLED;
}