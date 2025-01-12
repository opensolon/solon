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
package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

/**
 * IP 工具
 *
 * @author noear
 * @since 1.3
 * */
public class IpUtil {
    //
    // 可以进行替换扩展
    //
    private static IpUtil global = new IpUtil();

    public static IpUtil global() {
        return global;
    }

    public static void globalSet(IpUtil instance) {
        if (instance != null) {
            global = instance;
        }
    }


    /**
     * 获取 Ip
     * */
    public String getRealIp(Context ctx) {
        //客户端ip
        String ip = ctx.header("X-Real-IP");

        if (Utils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            //包含了客户端和各级代理ip的完整ip链路
            ip = ctx.headerOrDefault("X-Forwarded-For", "");
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }

        if (Utils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = ctx.remoteIp();
        }

        return ip;
    }
}
