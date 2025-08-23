package demo.statemachine.case2;

public enum OrderStatusEnum {
    WAIT_PAY, //待支付
    WAIT_DELIVER, //已支付待发货
    WAIT_RECEIVE, //已发货待收货
    RECEIVED; //已收货
}