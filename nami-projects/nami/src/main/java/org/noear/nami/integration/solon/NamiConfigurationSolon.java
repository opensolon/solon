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
package org.noear.nami.integration.solon;

import org.noear.nami.*;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LoadBalance;

/**
 * NamiConfiguration 的 solon 实现，实现与 solon LoadBalance 对接及注入处理
 *
 * @author noear
 * @since 1.2
 * */
public final class NamiConfigurationSolon implements NamiConfiguration {

    private NamiConfiguration custom;
    private AppContext context;

    public NamiConfigurationSolon(AppContext context) {
        this.context = context;

        //
        //如果有定制的NamiConfiguration, 则用之
        //
        context.getBeanAsync(NamiConfiguration.class, (bean) -> {
            custom = bean;
        });

        //订阅拦截器
        context.subBeansOfType(Filter.class, it -> {
            NamiManager.reg(it);
        });
    }

    @Override
    public void config(NamiClient client, NamiBuilder builder) {
        if (Utils.isEmpty(client.name())) {
            return;
        }

        //尝试自定义
        if (custom != null) {
            custom.config(client, builder);
        }

        //尝试从负载工厂获取（如果已提前指定，则跳过）
        if (builder.upstream() == null) {
            LoadBalance upstream = getUpstream(client);

            if (upstream != null) {
                builder.upstream(upstream::getServer);
            } else {
                //尝试从Ioc容器获取
                context.getWrapAsync(client.name(), (bw) -> {
                    LoadBalance tmp = bw.raw();
                    builder.upstream(tmp::getServer);
                });
            }
        }
    }

    private LoadBalance getUpstream(NamiClient anno) {
        return LoadBalance.get(anno.group(), anno.name());
    }
}
