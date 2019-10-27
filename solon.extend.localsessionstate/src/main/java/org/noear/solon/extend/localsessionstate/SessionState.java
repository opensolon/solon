package org.noear.solon.extend.localsessionstate;

import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XSessionState;
import org.noear.solon.extend.localsessionstate.util.EncryptUtil;
import org.noear.solon.extend.localsessionstate.util.IDUtil;
import org.noear.solon.extend.localsessionstate.util.ScheduledStore;


public class SessionState implements XSessionState {
    public final static String SESSIONID_KEY = "SOLONID";
    public final static String SESSIONID_MD5(){return SESSIONID_KEY+"2";}
    public final static String SESSIONID_encrypt = "&L8e!@T0";

    private final ScheduledStore _store;
    public SessionState(){

        _expiry = Aop.prop().getInt("solon.session.state.expiry",_expiry);
        _domain = Aop.prop().get("solon.session.state.domain",_domain);

        _store = new ScheduledStore(_expiry);

    }

    //
    // cookies control
    //
    private int _expiry =  60 * 60 * 2;
    private String _domain=null;

    public  String cookieGet(String key){
        return XContext.current().cookie(key);
    }
    public  void   cookieSet(String key, String val) {
        if (XUtil.isEmpty(_domain)) {
            _domain = XContext.current().uri().getHost();
        }

        XContext.current().cookieSet(key, val, _domain, _expiry);
    }

    protected void updateSessionID() {
        String skey = cookieGet(SESSIONID_KEY);

        if (XUtil.isEmpty(skey) == false) {
            cookieSet(SESSIONID_KEY, skey);
            cookieSet(SESSIONID_MD5(), EncryptUtil.md5(skey + SESSIONID_encrypt));

            _store.delay(sessionId());
        }
    }

    //
    // session control
    //

    @Override
    public String sessionId() {
        String skey = cookieGet(SESSIONID_KEY);
        String smd5 = cookieGet(SESSIONID_MD5());

        if(XUtil.isEmpty(skey)==false && XUtil.isEmpty(smd5)==false) {
            if (EncryptUtil.md5(skey + SESSIONID_encrypt).equals(smd5)) {
                return skey;
            }
        }

        skey = IDUtil.guid();
        cookieSet(SESSIONID_KEY,skey);
        cookieSet(SESSIONID_MD5(), EncryptUtil.md5(skey + SESSIONID_encrypt));
        return skey;
    }

    @Override
    public Object sessionGet(String key) {
        return _store.get(sessionId(),key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        _store.put(sessionId(),key,val);
    }
}
