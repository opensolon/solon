package org.noear.solon.extend.shiro.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.aop.RoleAnnotationHandler;
import org.noear.solon.core.handle.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tomsun28
 * @date 2021/5/12 23:20
 */
public class RoleInterceptor extends AbstractInterceptor<RequiresRoles> {
    private static final Logger log = LoggerFactory.getLogger(RoleInterceptor.class);
    public static final RoleInterceptor instance = new RoleInterceptor();

    private final RoleAnnotationHandler roleAnnotationHandler = new RoleAnnotationHandler();

    @Override
    public Class<RequiresRoles> type() {
        return RequiresRoles.class;
    }

    @Override
    public Result validate(RequiresRoles annotation) {
        try {
            roleAnnotationHandler.assertAuthorized(annotation);
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
