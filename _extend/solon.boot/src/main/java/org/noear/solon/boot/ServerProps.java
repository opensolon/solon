package org.noear.solon.boot;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * @author noear
 * @since 1.6
 * */
public class ServerProps {
    /**
     * 是否输出元信息
     * */
    public static final boolean output_meta;

    /**
     * 请求编码
     * */
    public static final String request_encoding;
    /**
     * 请求最大主体大小
     * */
    public static final int request_maxBodySize;
    /**
     * 请求最大头大小
     * */
    public static final int request_maxHeaderSize;

    /**
     * 会话超时
     * */
    public static final int session_timeout;
    /**
     * 会话状态域
     * */
    public static final String session_state_domain;


    /**
     * 响应编码
     * */
    public static final String response_encoding;

    static {
        String tmp = null;
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;



        //
        // for server ssl
        //
        synProps(ServerConstants.SERVER_KEY_STORE, ServerConstants.SSL_KEYSTORE);
        synProps(ServerConstants.SERVER_KEY_TYPE, ServerConstants.SSL_KEYSTORE_TYPE);
        synProps(ServerConstants.SERVER_KEY_PASSWORD, ServerConstants.SSL_KEYSTORE_PASSWORD);


        //
        // for request
        //
        tmp = Solon.cfg().get("server.request.maxBodySize", "").trim().toLowerCase();//k数
        if (Utils.isEmpty(tmp)) {
            //兼容旧的配置
            tmp = Solon.cfg().get("server.request.maxRequestSize", "").trim().toLowerCase();//k数
        }
        request_maxBodySize = getSize(tmp);

        tmp = Solon.cfg().get("server.request.maxHeaderSize", "").trim().toLowerCase();//k数
        request_maxHeaderSize = getSize(tmp);


        tmp = Solon.cfg().get("solon.request.encoding", "").trim();
        if (Utils.isEmpty(tmp)) {
            //兼容旧的配置 //@Deprecated
            tmp = Solon.cfg().get("solon.encoding.request", "").trim();
        }
        if (Utils.isEmpty(tmp)) {
            request_encoding = Solon.encoding();
        } else {
            request_encoding = tmp;
        }

        //
        // for session
        //
        session_timeout = Solon.cfg().getInt("server.session.timeout", 0);
        session_state_domain = Solon.cfg().get("server.session.state.domain");


        //
        // for response
        //
        tmp = Solon.cfg().get("solon.request.response", "").trim();
        if (Utils.isEmpty(tmp)) {
            //兼容旧的配置  //@Deprecated
            tmp = Solon.cfg().get("solon.encoding.response", "").trim();
        }
        if (Utils.isEmpty(tmp)) {
            response_encoding = Solon.encoding();
        } else {
            response_encoding = tmp;
        }
    }

    static void synProps(String appProp, String sysProp) {
        String tmp = Solon.cfg().get(appProp);
        if (tmp != null) {
            System.setProperty(sysProp, tmp);
        }
    }

    static int getSize(String tmp) {
        if (tmp == null) {
            return 0;
        }

        if (tmp.endsWith("mb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            return val * 1204 * 1204;
        } else if (tmp.endsWith("kb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            return val * 1204;
        } else if (tmp.length() > 0) {
            return Integer.parseInt(tmp); //支持-1
        } else {
            return 0;//默认0，表示不设置
        }
    }
}
