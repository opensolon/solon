package org.noear.solon.core.handle;

import org.noear.solon.Utils;

/**
 * @author noear
 * @since 1.0
 */
public class SessionStateDefault implements SessionState{
    public final static String SESSIONID_KEY = "SOLONID";
    public final static String SESSIONID_MD5() {
        return SESSIONID_KEY + "2";
    }
    public final static String SESSIONID_salt = "&L8e!@T0";

    @Override
    public String sessionId() {
        return null;
    }

    @Override
    public String sessionChangeId() {
        return null;
    }

    @Override
    public Object sessionGet(String key) {
        return null;
    }

    @Override
    public void sessionSet(String key, Object val) {

    }

    @Override
    public void sessionClear() {

    }

    @Override
    public void sessionReset() {

    }
}
