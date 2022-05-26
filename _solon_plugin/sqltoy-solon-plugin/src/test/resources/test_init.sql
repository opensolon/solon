create table SQLTOY_FRUIT_ORDER
(
    FRUIT_NAME           varchar(100) not null  comment '水果名称',
    ORDER_MONTH          integer not null  comment '订单月份',
    SALE_COUNT           numeric(10,2) not null  comment '销售数量',
    SALE_PRICE           numeric(10,2) not null  comment '销售单价',
    TOTAL_AMT            numeric(10,2) not null  comment '总金额',
    primary key (FRUIT_NAME, ORDER_MONTH)
);

alter table SQLTOY_FRUIT_ORDER comment '查询汇总演示-水果订单表';
