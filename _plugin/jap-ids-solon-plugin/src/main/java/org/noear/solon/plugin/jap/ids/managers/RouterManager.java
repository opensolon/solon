package org.noear.solon.plugin.jap.ids.managers;

import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.ErrorEndpoint;
import com.fujieid.jap.ids.exception.IdsException;
import com.fujieid.jap.ids.exception.InvalidRequestException;
import org.noear.solon.SolonApp;
import org.noear.solon.plugin.jap.ids.http.controller.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 * @since 1.6
 */
public class RouterManager {

    public RouterManager(SolonApp app) {
        new ApprovalController(app);
        new AuthorizationController(app);
        new CheckSessionController(app);
        new DiscoveryController(app);
        new ErrorController(app);
        new LoginController(app);
        new LogoutController(app);
        new TokenController(app);
        new UserController(app);
        app.filter((ctx, chain) -> {
            try {
                chain.doFilter(ctx);
                // 抛出验证异常
                HttpServletRequest request = (HttpServletRequest) ctx.request();
                if (request.getAttribute("javax.servlet.error.exception") != null) {
                    throw (Throwable) request.getAttribute("javax.servlet.error.exception");
                }
            } catch (IdsException exception) {
                new ErrorEndpoint().showErrorPage(
                        exception.getError(),
                        exception.getErrorDescription(),
                        new JakartaResponseAdapter((HttpServletResponse) ctx.response())
                );
            }
        });
    }

}
