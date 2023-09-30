package com.swagger.demo.model;

/**
 * @author noear 2023/9/3 created
 */
public enum OrderType {
    TYPE1(1, "a", "aaa"),
    TYPE2(2, "b", "bbb"),
    ;
    int code;
    String name;
    String description;

    OrderType(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
