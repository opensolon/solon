package com.fujieid.jap.solon;

import org.noear.solon.core.handle.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * @author noear
 * @since 1.6
 */
public class HttpServletRequestWrapperImpl extends HttpServletRequestWrapper {
    Context ctx;
    HttpServletRequest req;

    public HttpServletRequestWrapperImpl(Context ctx, HttpServletRequest request) {
        super(request);
        this.ctx = ctx;
        this.req = request;
    }

    @Override
    public HttpSession getSession() {
        return new HttpSessionImpl(req, ctx.sessionState());
    }

    @Override
    public HttpSession getSession(boolean create) {
        return new HttpSessionImpl(req, ctx.sessionState());
    }
}
