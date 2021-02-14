package org.noear.solon.extend.sessionstate.jwt;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateFactory;

/**
 * @author noear
 * @since 1.3
 */
public class JwtSessionStateFactory implements SessionStateFactory {
    private static JwtSessionStateFactory instance;
    public static JwtSessionStateFactory getInstance() {
        if (instance == null) {
            instance = new JwtSessionStateFactory();
        }

        return instance;
    }

    private JwtSessionStateFactory(){

    }



    public static final int SESSION_STATE_PRIORITY = 2;

    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }


    @Override
    public SessionState create(Context ctx) {
        return new JwtSessionState(ctx);
    }
}
