package org.noear.solon.extend.redissessionstate;

import org.noear.snack.ONode;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XMap;
import org.noear.solon.core.XSessionState;
import org.noear.solon.extend.redissessionstate.util.EncryptUtil;
import org.noear.solon.extend.redissessionstate.util.IDUtil;
import org.noear.solon.extend.redissessionstate.util.RedisX;

public class SessionState implements XSessionState {
    public final static String SESSIONID_KEY = "solon.id";
    public final static String SESSIONID_MD5(){return SESSIONID_KEY+"2";}
    public final static String SESSIONID_encrypt = "&L8e!@T0";

    private final RedisX redisX;
    public SessionState(){
        XMap map = Aop.prop().getXmap("solon.session.state.redis");

        if(map.size() < 5){
            throw new RuntimeException("Error configuration: solon.session.state.redis");
        }

        redisX = new RedisX(
                map.get("server"),
                map.get("user"),
                map.get("password"),
                map.getInt("db"),
                map.getInt("maxTotaol"));
    }

    //
    // cookies control
    //
    public  int expiry(){
        return 12;
    }
    public  String domain(){
        return "";
    }
    public  String cookieGet(String key){
        return XContext.current().cookie(key);
    }
    public  void   cookieSet(String key, String val) {
        XContext.current().cookieSet(key, val, domain(), -1);
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
        String tmp = redisX.open1((ru) -> ru.key(sessionId()).expire(expiry()).hashGet(key));

        if(tmp == null){
            return tmp;
        }

        Object val = null;
        try {
            val = ONode.deserialize(tmp, Object.class);
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

        redisX.open0((ru)->ru.key(sessionId()).expire(expiry()).hashSet(key,json));
    }
}
