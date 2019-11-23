package org.noear.solon.extend.sessionstate.redis;

import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Feature;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XMap;
import org.noear.solon.core.XSessionState;

/**
 * 它会是个单例，不能有上下文数据
 * */
public class SessionState implements XSessionState {
    public final static String SESSIONID_KEY = "SOLONID";
    public final static String SESSIONID_MD5(){return SESSIONID_KEY+"2";}
    public final static String SESSIONID_encrypt = "&L8e!@T0";

    private final RedisX redisX;
    public SessionState(){
        XMap map = Aop.prop().getXmap("server.session.state.redis");

        if(map.size() < 4){
            throw new RuntimeException("Error configuration: solon.session.state.redis");
        }

        if (XServerProp.session_timeout > 0) {
            _expiry = XServerProp.session_timeout;
        }

        if (XServerProp.session_state_domain != null) {
            _domain = XServerProp.session_state_domain;
        }

        redisX = new RedisX(
                map.get("server"),
                map.get("password"),
                map.getInt("db"),
                map.getInt("maxTotaol"));

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

            redisX.open0((ru)->ru.key(sessionId()).expire(_expiry).delay());
        }
    }

    //
    // session control
    //


    @Override
    public boolean replaceable() {
        return false;
    }

    @Override
    public String sessionId() {
        String _sessionId = XContext.current().attr("sessionId",null);

        if(_sessionId == null){
            _sessionId = sessionId_get();
            XContext.current().attrSet("sessionId",_sessionId);
        }

        return _sessionId;
    }

    private String sessionId_get() {
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
        String tmp = redisX.open1((ru) -> ru.key(sessionId()).expire(_expiry).hashGet(key));

        if(tmp == null){
            return tmp;
        }

        Object val = null;
        try {
            val = ONode.deserialize(tmp);
        }catch (Exception ex){
            throw new RuntimeException("Session state deserialization error: "+ key);
        }

        return val;
    }

    @Override
    public void sessionSet(String key, Object val) {
        String tmp = null;
        try {
            tmp = ONode.serialize(val);
        }catch (Exception ex){
            throw new RuntimeException("Session state serialization error: "+ key);
        }

        String json = tmp;

        redisX.open0((ru)->ru.key(sessionId()).expire(_expiry).hashSet(key,json));
    }

    @Override
    public void sessionClear() {
        redisX.open0((ru)->ru.key(sessionId()).delete());
    }
}
