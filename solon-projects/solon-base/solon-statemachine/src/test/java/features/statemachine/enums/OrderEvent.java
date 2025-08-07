package features.statemachine.enums;


import org.noear.solon.statemachine.Event;

/**
 * 订单事件枚举
 */
public enum OrderEvent implements Event {
    CREATE, PAY, SHIP, DELIVER, CANCEL;
}