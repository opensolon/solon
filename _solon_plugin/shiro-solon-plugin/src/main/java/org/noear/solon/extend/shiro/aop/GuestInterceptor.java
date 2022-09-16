package org.noear.solon.extend.shiro.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.aop.GuestAnnotationHandler;
import org.noear.solon.core.handle.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tomsun28
 * @date 2021/5/12 23:20
 */
public class GuestInterceptor extends AbstractInterceptor<RequiresGuest> {
    private static final Logger log = LoggerFactory.getLogger(GuestInterceptor.class);
    public static final GuestInterceptor instance = new GuestInterceptor();

    private final GuestAnnotationHandler handler = new GuestAnnotationHandler();

    @Override
    public Class<RequiresGuest> type() {
        return RequiresGuest.class;
    }

    @Override
    public Result validate(RequiresGuest annotation) {
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
