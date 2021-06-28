package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;

import java.util.List;

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

    default void add(Mapping mapping, List<MethodType> methodTypes, Handler handler){
        String path = Utils.annoName(mapping.value(), mapping.path());

        for (MethodType m1 : methodTypes) {
            if (mapping.after() || mapping.before()) {
                if (mapping.after()) {
                    after(path, m1, mapping.index(), handler);
                } else {
                    before(path, m1, mapping.index(), handler);
                }
            } else {
                add(path, m1, handler);
            }
        }
    };
}
