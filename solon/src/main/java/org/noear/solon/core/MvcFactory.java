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
     * 创建动作加载器
     */
    ActionLoader createLoader(BeanWrap wrap);

    /**
     * 创建动作加载器
     */
    default ActionLoader createLoader(BeanWrap wrap, String mapping) {
        return createLoader(wrap, mapping, wrap.remoting(), null, true);
    }

    /**
     * 创建动作加载器
     */
    default ActionLoader createLoader(BeanWrap wrap, String mapping, boolean remoting) {
        return createLoader(wrap, mapping, remoting, null, true);
    }

    /**
     * 创建动作加载器
     */
    ActionLoader createLoader(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping);

    /**
     * 查找动作方式类型
     */
    Set<MethodType> findMethodTypes(Set<MethodType> list, Predicate<Class> checker);

    /**
     * 分析动作参数
     */
    void resolveParam(ActionParam vo, AnnotatedElement element);

    /**
     * 获取动作默认执行器
     */
    ActionExecuteHandler getExecuteHandlerDefault();
}
