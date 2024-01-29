package org.noear.solon.core;

import org.noear.solon.core.handle.*;

import java.lang.reflect.AnnotatedElement;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Mvc 工厂
 *
 * @author noear
 * @since 2.7
 */
public interface MvcFactory {
    /**
     * 创建处理加载器
     * */
    HandlerLoader createHandlerLoader(BeanWrap wrap);

    /**
     * 创建处理加载器
     * */
    default HandlerLoader createHandlerLoader(BeanWrap wrap, String mapping) {
        return createHandlerLoader(wrap, mapping, wrap.remoting(), null, true);
    }

    /**
     * 创建处理加载器
     * */
    default HandlerLoader createHandlerLoader(BeanWrap wrap, String mapping, boolean remoting) {
        return createHandlerLoader(wrap, mapping, remoting, null, true);
    }

    /**
     * 创建处理加载器
     * */
    HandlerLoader createHandlerLoader(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping);

    /**
     * 查找方式类型
     * */
    Set<MethodType> findMethodTypes(Set<MethodType> list, Predicate<Class> checker);

    /**
     * 分析动作参数
     * */
    void resolveActionParam(ActionParam vo, AnnotatedElement element);

    ActionExecuteHandler getExecuteHandlerDefault();
}
