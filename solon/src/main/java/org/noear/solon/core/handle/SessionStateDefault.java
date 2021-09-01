package org.noear.solon.core.handle;

/**
 * @author noear 2021/2/14 created
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
    public Object sessionGet(String key) {
        return null;
    }

    @Override
    public void sessionSet(String key, Object val) {

    }

    @Override
    public void sessionClear() {

    }
}
