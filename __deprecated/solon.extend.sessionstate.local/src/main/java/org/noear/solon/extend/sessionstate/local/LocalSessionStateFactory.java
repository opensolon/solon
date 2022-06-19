package org.noear.solon.extend.sessionstate.local;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateFactory;

/**
 * @author noear 2021/2/14 created
 */
public class LocalSessionStateFactory implements SessionStateFactory {
    private static LocalSessionStateFactory instance;
    public static LocalSessionStateFactory getInstance() {
        if (instance == null) {
            instance = new LocalSessionStateFactory();
        }

        return instance;
    }
    private LocalSessionStateFactory(){

    }

    public static final int SESSION_STATE_PRIORITY = 1;
    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }

    @Override
    public SessionState create(Context ctx) {
        return new LocalSessionState(ctx);
    }

}
