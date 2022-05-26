package org.noear.solon.extend.shiro.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.noear.solon.core.handle.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tomsun28
 * @date 2021/5/12 23:20
 */
public class PermissionInterceptor extends AbstractInterceptor<RequiresPermissions> {
    private static final Logger log = LoggerFactory.getLogger(PermissionInterceptor.class);
    public static final PermissionInterceptor instance = new PermissionInterceptor();

    private final PermissionAnnotationHandler handler = new PermissionAnnotationHandler();

    @Override
    public Class<RequiresPermissions> type() {
        return RequiresPermissions.class;
    }

    @Override
    public Result validate(RequiresPermissions annotation) {
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
