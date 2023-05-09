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
public class UserInterceptor extends AbstractInterceptor<RequiresUser> {
    private static final Logger log = LoggerFactory.getLogger(UserInterceptor.class);
    public static final UserInterceptor instance = new UserInterceptor();

    private final UserAnnotationHandler handler = new UserAnnotationHandler();

    @Override
    public Class<RequiresUser> type() {
        return RequiresUser.class;
    }

    @Override
    public Result validate(RequiresUser annotation) {
        try {
            handler.assertAuthorized(annotation);
        } catch (AuthorizationException e) {
            log.warn(e.getMessage());
            return Result.failure(403, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.failure(e.getMessage());
        }
        return Result.succeed();
    }
}
