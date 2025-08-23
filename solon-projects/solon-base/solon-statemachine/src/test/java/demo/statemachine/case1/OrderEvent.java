package demo.statemachine.case1;

public enum OrderEvent {
    CREATE_ORDER,//创建订单
    PAY_ORDER,//支付订单
    RECEIVE_ORDER,//取货
    CANCEL_ORDER,//取消订单
    FINISH_ORDER//完成订单
}