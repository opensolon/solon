package org.noear.solon.boot;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * 服务公共属性
 *
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
     * 请求最大头大小
     * */
    public static final int request_maxHeaderSize;
    /**
     * 请求最大主体大小
     * */
    public static final long request_maxBodySize;
    /**
     * 分片最大文件大小
     * */
    public static final long request_maxFileSize;
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

    public static void init(){
        //空的，别去掉
    }

    static {
        String tmp = null;
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;

        //
        // for request
        //

        tmp = Solon.cfg().get("server.request.maxHeaderSize", "").trim().toLowerCase();//k数
        request_maxHeaderSize = (int)getSize(tmp, 8192L);//8k

        tmp = Solon.cfg().get("server.request.maxBodySize", "").trim().toLowerCase();//k数
        if (Utils.isEmpty(tmp)) {
            //兼容旧的配置
            tmp = Solon.cfg().get("server.request.maxRequestSize", "").trim().toLowerCase();//k数
        }
        request_maxBodySize = getSize(tmp, 2097152L);//2m

        tmp = Solon.cfg().get("server.request.maxFileSize", "").trim().toLowerCase();//k数
        if (Utils.isEmpty(tmp)) {
            request_maxFileSize = request_maxBodySize;
        } else {
            request_maxFileSize = getSize(tmp, 2097152L);//2m
        }

        tmp = Solon.cfg().get("server.request.encoding", "").trim();

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
        tmp = Solon.cfg().get("server.response.encoding", "").trim();

        if (Utils.isEmpty(tmp)) {
            response_encoding = Solon.encoding();
        } else {
            response_encoding = tmp;
        }
    }

    static void synProps(String appProp, String sysProp) {
        String tmp = Solon.cfg().get(appProp);
        if (tmp != null) {
            System.getProperties().putIfAbsent(sysProp, tmp);
        }
    }

    static long getSize(String tmp, long def) {
        if (tmp == null) {
            return def;
        }

        if (tmp.endsWith("mb")) {
            long val = Long.parseLong(tmp.substring(0, tmp.length() - 2));
            return val * 1204 * 1204;
        } else if (tmp.endsWith("kb")) {
            long val = Long.parseLong(tmp.substring(0, tmp.length() - 2));
            return val * 1204;
        } else if (tmp.length() > 0) {
            return Long.parseLong(tmp); //支持-1
        } else {
            return def;//默认0，表示不设置
        }
    }
}
