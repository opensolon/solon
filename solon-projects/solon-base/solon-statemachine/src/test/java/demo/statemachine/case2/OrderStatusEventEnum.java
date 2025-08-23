package demo.statemachine.case2;

public enum OrderStatusEventEnum {
    ORDER, //用户下单
    PAY, //用户支付成功
    DELIVER, //仓库已发货
    RECEIVE; //用户成功收货
}