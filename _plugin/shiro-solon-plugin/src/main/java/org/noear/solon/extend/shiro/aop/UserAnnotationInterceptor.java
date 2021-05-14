package org.noear.solon.extend.shiro.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.authz.aop.UserAnnotationHandler;
import org.noear.solon.core.handle.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tomsun28
 * @date 2021/5/12 23:20
 */
public class UserAnnotationInterceptor extends AbstractInterceptor<RequiresUser> {

    private static final Logger logger = LoggerFactory.getLogger(UserAnnotationInterceptor.class);
    private final UserAnnotationHandler handler = new UserAnnotationHandler();

    public static final UserAnnotationInterceptor instance = new UserAnnotationInterceptor();

    @Override
    public Class<RequiresUser> type() {
        return RequiresUser.class;
    }

    @Override
    public Result validate(RequiresUser annotation) {
        try {
            handler.assertAuthorized(annotation);
        } catch (AuthorizationException e) {
            logger.warn(e.getMessage());
            return Result.failure(403);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Result.failure(e.getMessage());
        }
        return Result.succeed();
    }
}
