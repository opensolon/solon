/*
 * Copyright 2017-2024 noear.org and authors
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
import org.noear.nami.common.InfoUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear
 * @since 1.2
 * */
public class NamiPlugin implements Plugin {
    private Map<NamiClient, Object> cached = new ConcurrentHashMap<>();

    @Override
    public void start(AppContext context) {
        if (NamiConfigurationDefault.proxy == null) {
            NamiConfigurationDefault.proxy = new NamiConfigurationSolon(context);
        }

        context.beanInjectorAdd(NamiClient.class, (vh, anno) -> {
            vh.required(true);

            if (vh.getType().isInterface() == false) {
                return;
            }

            boolean localFirst = anno.localFirst();

            if (Utils.isEmpty(anno.url()) && Utils.isEmpty(anno.name())) {
                NamiClient anno2 = vh.getType().getAnnotation(NamiClient.class);
                if (anno2 != null) {
                    anno = anno2;
                }
            }

            localFirst |= anno.localFirst();

            if (localFirst) {
                //如果本地优化，开始找 Bean；如果找到就替换注入目标
                context.getBeanAsync(vh.getType(), bean -> {
                    vh.setValue(bean);
                });

                if (vh.isDone()) {
                    //如果已注入完成
                    return;
                }
            }


            //代理一下，把 name 改掉
            anno = new NamiClientAnno(anno);

            if (Utils.isEmpty(anno.url()) && Utils.isEmpty(anno.name()) && anno.upstream().length == 0) {
                throw new NamiException("@NamiClient configuration error: " + vh.getFullName());
            } else {
                InfoUtils.print(vh.getType(), anno);
            }

            Object obj = cached.computeIfAbsent(anno, k -> {
                return Nami.builder().create(vh.getType(), k);
            });

            vh.setValue(obj);
        });
    }
}
