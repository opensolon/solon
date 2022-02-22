package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.JapUser;
import com.fujieid.jap.core.context.JapAuthentication;
import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.solon.HttpServletRequestWrapperImpl;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 * @since 1.6
 */
public class AccountController extends JapController {
    @Get
    @Mapping("/current")
    public JapUser current(Context ctx, HttpServletRequest request, HttpServletResponse response) {
        request = new HttpServletRequestWrapperImpl(ctx, request);

        try {
            return JapAuthentication.getUser(
                    new JakartaRequestAdapter(request),
                    new JakartaResponseAdapter(response)
            ).setPassword(null);
        } catch (NullPointerException ignore) {
            ctx.status(401);
            return null;
        }
    }
}
