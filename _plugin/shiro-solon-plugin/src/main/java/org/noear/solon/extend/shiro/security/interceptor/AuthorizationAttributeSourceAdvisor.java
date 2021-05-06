package org.noear.solon.extend.shiro.security.interceptor;

import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author noear 2021/5/6 created
 */
//public class AuthorizationAttributeSourceAdvisor {
//    private static final Logger log = LoggerFactory.getLogger(AuthorizationAttributeSourceAdvisor.class);
//    private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES = new Class[]{RequiresPermissions.class, RequiresRoles.class, RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class};
//    protected SecurityManager securityManager = null;
//
//    public AuthorizationAttributeSourceAdvisor() {
//        //this.setAdvice(new AopAllianceAnnotationsAuthorizingMethodInterceptor());
//    }
//
//    public SecurityManager getSecurityManager() {
//        return this.securityManager;
//    }
//
//    public void setSecurityManager(SecurityManager securityManager) {
//        this.securityManager = securityManager;
//    }
//
//    public boolean matches(Method method, Class targetClass) {
//        Method m = method;
//        if (this.isAuthzAnnotationPresent(method)) {
//            return true;
//        } else {
//            if (targetClass != null) {
//                try {
//                    m = targetClass.getMethod(m.getName(), m.getParameterTypes());
//                    return this.isAuthzAnnotationPresent(m) || this.isAuthzAnnotationPresent(targetClass);
//                } catch (NoSuchMethodException var5) {
//                }
//            }
//
//            return false;
//        }
//    }
//
//    private boolean isAuthzAnnotationPresent(Class<?> targetClazz) {
//        Class[] var2 = AUTHZ_ANNOTATION_CLASSES;
//        int var3 = var2.length;
//
//        for(int var4 = 0; var4 < var3; ++var4) {
//            Class<? extends Annotation> annClass = var2[var4];
//            Annotation a = targetClazz.getAnnotation(annClass);
//            if (a != null) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private boolean isAuthzAnnotationPresent(Method method) {
//        Class[] var2 = AUTHZ_ANNOTATION_CLASSES;
//        int var3 = var2.length;
//
//        for(int var4 = 0; var4 < var3; ++var4) {
//            Class<? extends Annotation> annClass = var2[var4];
//            Annotation a = method.getAnnotation(annClass);
//            if (a != null) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//}
