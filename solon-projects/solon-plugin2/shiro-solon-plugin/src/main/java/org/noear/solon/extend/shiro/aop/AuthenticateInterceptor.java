package org.noear.solon.extend.shiro.aop;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.aop.AuthenticatedAnnotationHandler;
import org.noear.solon.core.handle.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tomsun28
 * @date 2021/5/12 23:20
 */
public class AuthenticateInterceptor extends AbstractInterceptor<RequiresAuthentication> {
    private static final Logger log = LoggerFactory.getLogger(AuthenticateInterceptor.class);
    public static final AuthenticateInterceptor instance = new AuthenticateInterceptor();

    private final AuthenticatedAnnotationHandler handler = new AuthenticatedAnnotationHandler();

    @Override
    public Class<RequiresAuthentication> type() {
        return RequiresAuthentication.class;
    }

    @Override
    public Result validate( RequiresAuthentication annotation) {
        try {
            handler.assertAuthorized(annotation);
        } catch (UnauthenticatedException e) {
            log.warn(e.getMessage());
            return Result.failure(401, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.failure(e.getMessage());
        }
        return Result.succeed();
    }
}
