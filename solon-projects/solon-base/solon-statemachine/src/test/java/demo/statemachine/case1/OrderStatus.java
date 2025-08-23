package demo.statemachine.case1;


public enum OrderStatus {
    WAITING_PAYMENT,//待支付
    WAITING_RECEIVE,//待取货
    FINISHED,//已完成
    CANCELED;//已取消
}