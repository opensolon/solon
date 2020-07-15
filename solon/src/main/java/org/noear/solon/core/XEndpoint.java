package org.noear.solon.core;

/**
 * 处理点（配合路由器使用）
 * */
public final class XEndpoint {
    /**前置处理*/
    public static final int before = 0;
    /**主体处理*/
    public static final int main   = 1;
    /**后置处理*/
    public static final int after  = 2;
}
