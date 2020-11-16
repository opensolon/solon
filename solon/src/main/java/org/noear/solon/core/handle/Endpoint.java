package org.noear.solon.core.handle;

import org.noear.solon.core.route.Router;

/**
 * 处理点（配合路由器使用）
 *
 * @see Router#add(String, Endpoint, MethodType, int, Handler)
 * @author noear
 * @since 1.0
 * */
public enum Endpoint {
    /**前置处理*/
    before(0),
    /**主体处理*/
    main(1),
    /**后置处理*/
    after(2);

    public final int code;
    Endpoint(int code){
        this.code = code;
    }
}
