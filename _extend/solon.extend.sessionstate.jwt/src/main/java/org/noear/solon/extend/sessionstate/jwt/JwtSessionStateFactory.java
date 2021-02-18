package org.noear.solon.extend.sessionstate.jwt;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
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

    private JwtSessionStateFactory() {
        String signKey0 = XServerProp.session_state_signKey;
        if (Utils.isNotEmpty(signKey0)) {
            signKey = signKey0;
        }

        requestUseHeader = XServerProp.session_state_requestUseHeader;
        responseUseHeader = XServerProp.session_state_responseUseHeader;
    }


    private String signKey = "DHPjbM5QczZ2cysd4gpDbG/4SnuwzWX3sA1i6AXiAbo=";

    /**
     * 获取签名Key
     * */
    public String signKey() {
        return signKey;
    }

    /**
     * 设置签名Key
     * */
    public void signKeySet(String key) {
        signKey = key;
    }


    private boolean requestUseHeader = false;
    /**
     * 请求时使用头
     * */
    public boolean requestUseHeader(){
        return requestUseHeader;
    }

    public void requestUseHeaderSet(boolean use){
        requestUseHeader = use;
    }

    private boolean responseUseHeader = false;
    /**
     * 响应时使用头
     * */
    public boolean responseUseHeader(){
        return responseUseHeader;
    }
    public void responseUseHeaderSet(boolean use){
        responseUseHeader = use;
    }


    public static final int SESSION_STATE_PRIORITY = 2;

    /**
     * 优先级
     * */
    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }


    /**
     * 创建会话状态
     * */
    @Override
    public SessionState create(Context ctx) {
        return new JwtSessionState(ctx);
    }
}
