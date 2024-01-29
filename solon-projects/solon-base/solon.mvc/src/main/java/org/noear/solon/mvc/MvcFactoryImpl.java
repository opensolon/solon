package org.noear.solon.mvc;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.MvcFactory;
import org.noear.solon.core.handle.*;

import java.lang.reflect.AnnotatedElement;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 通用处理接口加载器工厂
 *
 * @author noear
 * @since 2.4
 */
public class MvcFactoryImpl implements MvcFactory {
    public HandlerLoader createHandlerLoader(BeanWrap wrap) {
        return new HandlerLoaderImpl(wrap);
    }

    public HandlerLoader createHandlerLoader(BeanWrap wrap, String mapping) {
        return createHandlerLoader(wrap, mapping, wrap.remoting(), null, true);
    }

    public HandlerLoader createHandlerLoader(BeanWrap wrap, String mapping, boolean remoting) {
        return createHandlerLoader(wrap, mapping, remoting, null, true);
    }

    public HandlerLoader createHandlerLoader(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
        return new HandlerLoaderImpl(wrap, mapping, remoting, render, allowMapping);
    }

    @Override
    public Set<MethodType> findMethodTypes(Set<MethodType> list, Predicate<Class> checker) {
        return MethodTypeResolver.findAndFill(list, checker);
    }

    @Override
    public void resolveActionParam(ActionParam vo, AnnotatedElement element) {
        ActionParamResolver.resolve(vo, element);
    }

    private final ActionExecuteHandler executeHandlerDefault = new ActionExecuteHandlerDefault();

    @Override
    public ActionExecuteHandler getExecuteHandlerDefault() {
        return executeHandlerDefault;
    }
}
