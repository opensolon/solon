package org.noear.solon.extend.shiro.security.interceptor;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInterceptor;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.aop.*;
import org.noear.solon.extend.shiro.aop.SolonAnnotationResolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2021/5/6 created
 */
//public class AopAllianceAnnotationsAuthorizingMethodInterceptor extends AnnotationsAuthorizingMethodInterceptor implements MethodInterceptor {
//    public AopAllianceAnnotationsAuthorizingMethodInterceptor() {
//        List<AuthorizingAnnotationMethodInterceptor> interceptors = new ArrayList(5);
//        AnnotationResolver resolver = new SolonAnnotationResolver();
//        interceptors.add(new RoleAnnotationMethodInterceptor(resolver));
//        interceptors.add(new PermissionAnnotationMethodInterceptor(resolver));
//        interceptors.add(new AuthenticatedAnnotationMethodInterceptor(resolver));
//        interceptors.add(new UserAnnotationMethodInterceptor(resolver));
//        interceptors.add(new GuestAnnotationMethodInterceptor(resolver));
//        this.setMethodInterceptors(interceptors);
//    }
//
//    protected MethodInvocation createMethodInvocation(Object implSpecificMethodInvocation) {
//        final org.aopalliance.intercept.MethodInvocation mi = (org.aopalliance.intercept.MethodInvocation)implSpecificMethodInvocation;
//        return new MethodInvocation() {
//            public Method getMethod() {
//                return mi.getMethod();
//            }
//
//            public Object[] getArguments() {
//                return mi.getArguments();
//            }
//
//            public String toString() {
//                return "Method invocation [" + mi.getMethod() + "]";
//            }
//
//            public Object proceed() throws Throwable {
//                return mi.proceed();
//            }
//
//            public Object getThis() {
//                return mi.getThis();
//            }
//        };
//    }
//
//    protected Object continueInvocation(Object aopAllianceMethodInvocation) throws Throwable {
//        org.aopalliance.intercept.MethodInvocation mi = (org.aopalliance.intercept.MethodInvocation)aopAllianceMethodInvocation;
//        return mi.proceed();
//    }
//
//    public Object invoke(org.aopalliance.intercept.MethodInvocation methodInvocation) throws Throwable {
//        MethodInvocation mi = this.createMethodInvocation(methodInvocation);
//        return super.invoke(mi);
//    }
//}
