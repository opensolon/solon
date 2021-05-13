package org.noear.solon.extend.shiro.aop;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.aop.AuthenticatedAnnotationHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * @author tomsun28
 * @date 2021/5/12 23:20
 */
public class AuthenticateAnnotationInterceptor implements Validator<RequiresAuthentication> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticateAnnotationInterceptor.class);
    private final AuthenticatedAnnotationHandler handler = new AuthenticatedAnnotationHandler();

    public static final AuthenticateAnnotationInterceptor instance = new AuthenticateAnnotationInterceptor();

    @Override
    public Result validate(Context ctx, RequiresAuthentication annotation, String name, StringBuilder tmp) {
        try {
            handler.assertAuthorized(annotation);
        } catch (UnauthenticatedException e) {
            logger.warn(e.getMessage());
            return Result.failure(401);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Result.failure();
        }
        return Result.succeed();
    }
}
