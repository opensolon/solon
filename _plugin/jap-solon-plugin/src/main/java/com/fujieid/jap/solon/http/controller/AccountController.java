package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.JapUser;
import com.fujieid.jap.core.context.JapAuthentication;
import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.solon.HttpServletRequestWrapperImpl;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 */
public class AccountController extends JapController {

    @Mapping(path = "/current", method = MethodType.GET)
    public JapUser current(HttpServletRequest request, HttpServletResponse response) {
        request = new HttpServletRequestWrapperImpl(Context.current(), request);

        try {
            return JapAuthentication.getUser(
                    new JakartaRequestAdapter(request),
                    new JakartaResponseAdapter(response)
            ).setPassword(null);
        } catch (NullPointerException ignore) {
            Context.current().status(401);
            return null;
        }
    }

}
