package org.noear.solon.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理助手，提供前置与后置处理的存储
 * */
public abstract class XHandlerAide {
    /** 前置处理 */
    protected List<XHandler> _before  =new ArrayList<>();
    /** 后置处理 */
    protected List<XHandler> _after  =new ArrayList<>();

    /** 添加前置处理 */
    public void before(XHandler handler){
        _before.add(handler);
    }

    /** 添加后置处理 */
    public void after(XHandler handler){
        _after.add(handler);
    }
}
