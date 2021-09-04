package org.noear.solon.extend.sureness.integration;


import com.usthe.sureness.mgt.SurenessSecurityManager;
import com.usthe.sureness.processor.exception.*;
import com.usthe.sureness.subject.SubjectSum;
import com.usthe.sureness.util.SurenessContextHolder;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.core.handle.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * sureness filter class example, filter all http request
 * @author tomsun28
 * @date 2020-09-29 22:02
 */
@Component
public class SurenessFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SurenessFilter.class);

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {

        try {
            SubjectSum subject = SurenessSecurityManager.getInstance().checkIn(ctx);
            // You can consider using SurenessContextHolder to bind subject in threadLocal
            // if bind, please remove it when end
            if (subject != null) {
                SurenessContextHolder.bindSubject(subject);
            }
        } catch (IncorrectCredentialsException | UnknownAccountException | ExpiredCredentialsException e1) {
            logger.debug("this request is illegal");
            responseWrite(ctx, 401, e1.getMessage(), null);
            return;
        } catch (DisabledAccountException | ExcessiveAttemptsException e2) {
            logger.debug("the account is disabled");
            responseWrite(ctx, 401, e2.getMessage(), null);
            return;
        } catch (NeedDigestInfoException e5) {
            logger.debug("you should try once again with digest auth information");
            responseWrite(ctx, 401,
                    "try once again with digest auth information",
                    Collections.singletonMap("WWW-Authenticate", e5.getAuthenticate()));
            return;
        } catch (UnauthorizedException e5) {
            logger.debug("this account can not access this resource");
            responseWrite(ctx, 403, e5.getMessage(), null);
            return;
        } catch (RuntimeException e) {
            logger.error("other exception happen: ", e);
            responseWrite(ctx, 409, e.getMessage(), null);
            return;
        }
        chain.doFilter(ctx);
    }

    /**
     * write response data
     *
     * @param ctx        ctx
     * @param statusCode statusCode
     * @param message    message
     */
    private void responseWrite(Context ctx, int statusCode,
                               String message, Map<String, String> headers) throws Throwable {
        ctx.status(statusCode);
        if (headers != null) {
            headers.forEach(ctx::headerAdd);
        }

        ctx.render(Result.failure(message));
    }
}
