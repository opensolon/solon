/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.server;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 服务公共属性
 *
 * @author noear
 * @since 1.6
 * */
public class ServerProps {
    /**
     * 是否输出元信息
     */
    public static final boolean output_meta;
    /**
     * 请求编码
     */
    public static final String request_encoding;
    /**
     * 请求最大头大小
     */
    public static final int request_maxHeaderSize;
    /**
     * 请求最大主体大小
     */
    public static final long request_maxBodySize;
    /**
     * 上传最大文件大小
     */
    public static final long request_maxFileSize;
    /**
     * 上传使用临时文件
     */
    public static final boolean request_useTempfile;

    /**
     * 上传使用原始路径
     *
     * @since 2.8
     */
    public static final boolean request_useRawpath;

    /**
     * 响应编码
     */
    public static final String response_encoding;

    public static void init() {
        //空的，别去掉
    }

    static {
        String tmp = null;
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;

        //
        // for request
        //

        tmp = Solon.cfg().get(ServerConstants.SERVER_REQUEST_MAXHEADERSIZE, "").trim().toLowerCase();//k数
        request_maxHeaderSize = (int) getSize(tmp, 8192L);//8k

        tmp = Solon.cfg().get(ServerConstants.SERVER_REQUEST_MAXBODYSIZE, "").trim().toLowerCase();//k数
        if (Utils.isEmpty(tmp)) {
            //兼容旧的配置
            tmp = Solon.cfg().get("server.request.maxRequestSize", "").trim().toLowerCase();//k数
        }
        request_maxBodySize = getSize(tmp, 2097152L);//2m

        tmp = Solon.cfg().get(ServerConstants.SERVER_REQUEST_MAXFILESIZE, "").trim().toLowerCase();//k数
        if (Utils.isEmpty(tmp)) {
            request_maxFileSize = request_maxBodySize;
        } else {
            request_maxFileSize = getSize(tmp, 2097152L);//2m
        }

        tmp = Solon.cfg().get(ServerConstants.SERVER_REQUEST_ENCODING, "").trim();

        if (Utils.isEmpty(tmp)) {
            request_encoding = Solon.encoding();
        } else {
            request_encoding = tmp;
        }

        request_useTempfile = Solon.cfg().getBool(ServerConstants.SERVER_REQUEST_USETEMPFILE, false);

        request_useRawpath = Solon.cfg().getBool(ServerConstants.SERVER_REQUEST_USERAWPATH, false);


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

    public static String urlDecode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, request_encoding);
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
