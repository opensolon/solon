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
package org.noear.solon.cloud.gateway.route.predicate;

import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 路由 RemoteAddr 匹配检测器
 *
 * @author wfm
 * @since 2.9
 */
public class RemoteAddrPredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "RemoteAddr";
    }

    @Override
    public ExPredicate create(String config) {
        return new RemoteAddrPredicate(config);
    }

    public static class RemoteAddrPredicate implements ExPredicate {
        private final IpSubnetFilterRule rule;

        /**
         * @param config (RemoteAddr=192.168.1.1/24)
         */
        public RemoteAddrPredicate(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("RemoteAddr config cannot be blank");
            }

            String[] parts = config.split("/");
            String ip = parts[0];

            try {
                InetAddress address = InetAddress.getByName(ip);

                // 获取掩码
                int mask;
                if (parts.length > 1) {
                    mask = Integer.parseInt(parts[1]);
                } else {
                    mask = address instanceof Inet4Address ? 32 : 128;
                }

                rule = new IpSubnetFilterRule(address, mask, IpFilterRuleType.ACCEPT);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("RemoteAddr config is wrong: " + config, e);
            }
        }

        @Override
        public boolean test(ExContext ctx) {
            String ip = ctx.realIp();
            if (Utils.isEmpty(ip)) {
                return false;
            }

            // 只匹配ip，所以这里的端口无效
            return rule.matches(new InetSocketAddress(ip, 1));
        }
    }
}