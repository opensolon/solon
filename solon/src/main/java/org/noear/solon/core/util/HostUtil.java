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

import org.noear.solon.core.handle.Context;
import org.noear.solon.lang.Internal;

import java.net.URI;

/**
 * Host 工具（含真实 IP / 真实 Host 解析，支持反向代理）
 *
 * @author noear
 * @since 1.3
 * @since 4.0
 */
@Internal
public class HostUtil {
    //
    // 可以进行替换扩展
    //
    private static HostUtil global = new HostUtil();

    public static HostUtil global() {
        return global;
    }

    public static void globalSet(HostUtil instance) {
        if (instance != null) {
            global = instance;
        }
    }


    /**
     * 获取 Ip（支持反向代理）
     *
     */
    public String getRealIp(Context ctx) {
        //客户端ip
        String ip = ctx.header("X-Real-IP");

        if (Assert.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            //包含了客户端和各级代理ip的完整ip链路
            ip = ctx.headerOrDefault("X-Forwarded-For", "");
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }

        if (Assert.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = ctx.remoteIp();
        }

        return ip;
    }

    /**
     * 获取原始请求 Host（支持反向代理）
     * <p>
     * 解析顺序：{@code X-Forwarded-Host} → {@code Host} → {@code uri().getHost()}。
     * 多级代理时取第一个值；可能包含端口（如 {@code www.example.com:443}）。
     * 仅应在受信任的反向代理之后使用。
     *
     * @since 4.0.4
     */
    public String getRealHost(Context ctx) {
        // 1. 代理转发的原始主机
        String host = firstCsvHeader(ctx, "X-Forwarded-Host");
        
        // 2. 请求 Host
        if (Assert.isEmpty(host) || "unknown".equalsIgnoreCase(host)) {
            host = firstCsvHeader(ctx, "Host");
        }
        
        // 3. 回落到 URI host（不含端口）
        if (Assert.isEmpty(host) || "unknown".equalsIgnoreCase(host)) {
            URI uri = ctx.uri();
            host = (uri == null ? null : uri.getHost());
        }
            
        return host;
    }
        
    /**
     * 读取请求头，多值时取第一个并 trim
     */
    private String firstCsvHeader(Context ctx, String name) {
        String value = ctx.header(name);
        if (Assert.isEmpty(value)) {
            return value;
        }

        if (value.indexOf(',') > -1) {
            value = value.split(",")[0];
        }

        return value.trim();
    }
}