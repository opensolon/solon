package org.noear.solon.core;

/**
 * 处理点（配合路由器使用）
 *
 * @author noear
 * @since 1.0
 * */
public enum  XEndpoint {
    /**前置处理*/
    before(0),
    /**主体处理*/
    main(1),
    /**后置处理*/
    after(2);

    public final int code;
    XEndpoint(int code){
        this.code = code;
    }
}
