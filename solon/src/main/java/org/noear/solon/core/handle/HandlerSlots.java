package org.noear.solon.core.handle;

import org.noear.solon.annotation.Mapping;

/**
 * 通用处理接口接收槽
 *
 * @author noear
 * @since 1.0
 * */
public interface HandlerSlots {
    /**
     * 添加前置处理
     * */
    default void before(String expr, MethodType method, int index, Handler handler){}
    /**
     * 添加后置处理
     * */
    default void after(String expr, MethodType method, int index, Handler handler){};
    /**
     * 添加主体处理
     * */
    void add(String expr, MethodType method, Handler handler);

    default void add(Mapping mapping, Handler handler){
        for (MethodType m1 : mapping.method()) {
            if (mapping.after() || mapping.before()) {
                if (mapping.after()) {
                    after(mapping.value(), m1, mapping.index(), handler);
                } else {
                    before(mapping.value(), m1, mapping.index(), handler);
                }
            } else {
                add(mapping.value(), m1, handler);
            }
        }
    };
}
