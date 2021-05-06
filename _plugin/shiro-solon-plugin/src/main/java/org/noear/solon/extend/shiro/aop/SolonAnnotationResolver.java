package org.noear.solon.extend.shiro.aop;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author noear 2021/5/6 created
 */
public class SolonAnnotationResolver implements AnnotationResolver {
    public Annotation getAnnotation(MethodInvocation mi, Class<? extends Annotation> clazz) {
        Method m = mi.getMethod();
        return m.getAnnotation(clazz);
    }
}
