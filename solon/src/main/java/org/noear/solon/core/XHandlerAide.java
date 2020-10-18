package org.noear.solon.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理助手，提供前置与后置处理的存储
 *
 * @see org.noear.solon.XGateway
 * @see XAction
 * @author noear
 * @since 1.0
 * */
public class XHandlerAide {
    /** 前置处理 */
    protected List<XHandler> befores =new ArrayList<>();
    /** 后置处理 */
    protected List<XHandler> afters =new ArrayList<>();

    /** 添加前置处理 */
    public void before(XHandler handler){
        befores.add(handler);
    }

    /** 添加后置处理 */
    public void after(XHandler handler){
        afters.add(handler);
    }
}
