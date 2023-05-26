package org.noear.solon.docs.openapi2;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.Routing;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.3
 */
public class ActionHolder {
    private final Routing<Handler> routing;
    private final Action action;

    public Routing<Handler> routing(){
        return routing;
    }

    public Action action(){
        return action;
    }

    public Class<?> controllerClz(){
        return action.controller().clz();
    }

    public boolean isGet() {
        return routing().method() == MethodType.GET || action.method().getParamWraps().length ==0;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annoClz){
        return action.method().isAnnotationPresent(annoClz);
    }

    public <T extends Annotation> T  getAnnotation(Class<T> annoClz){
        return action.method().getAnnotation(annoClz);
    }


    public <T extends Annotation> T[]  getAnnotationsByType(Class<T> annoClz){
        return action.method().getMethod().getAnnotationsByType(annoClz);
    }

    public ActionHolder(Routing<Handler> routing, Action action){
        this.routing = routing;
        this.action =action;
    }

    @Override
    public String toString() {
        return action.fullName();
    }
}
