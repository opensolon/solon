package org.noear.solon.core.handler;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理助手，提供前置与后置处理的存储
 *
 * @see Gateway
 * @see Action
 * @author noear
 * @since 1.0
 * */
public class HandlerAide {
    /** 前置处理 */
    protected List<Handler> befores =new ArrayList<>();
    /** 后置处理 */
    protected List<Handler> afters =new ArrayList<>();

    /** 添加前置处理 */
    public void before(Handler handler){
        befores.add(handler);
    }

    /** 添加后置处理 */
    public void after(Handler handler){
        afters.add(handler);
    }
}
