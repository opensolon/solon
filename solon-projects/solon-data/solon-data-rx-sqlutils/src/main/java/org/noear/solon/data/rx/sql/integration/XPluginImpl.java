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
package org.noear.solon.data.rx.sql.integration;

import io.r2dbc.spi.ConnectionFactory;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.VarHolder;
import org.noear.solon.data.rx.sql.SqlConfiguration;
import org.noear.solon.data.rx.sql.RxSqlUtils;
import org.noear.solon.data.rx.sql.RxSqlUtilsFactory;

import java.util.function.Consumer;

/**
 * @author noear
 * @since 3.0
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        context.beanInjectorAdd(Inject.class, RxSqlUtils.class, this::doInject);

        context.getBeanAsync(RxSqlUtilsFactory.class, bean -> {
            SqlConfiguration.setFactory(bean);
        });
    }

    private void doInject(VarHolder vh, Inject anno) {
        vh.required(anno.required());

        this.observeDs(vh.context(), anno.value(), bw -> {
            vh.setValue(RxSqlUtils.of(bw.raw()));
        });
    }

    private void observeDs(AppContext context, String dsName, Consumer<BeanWrap> consumer) {
        if (Utils.isEmpty(dsName)) {
            context.getWrapAsync(ConnectionFactory.class, (dsBw) -> {
                consumer.accept(dsBw);
            });
        } else {
            context.getWrapAsync(dsName, (dsBw) -> {
                if (dsBw.raw() instanceof ConnectionFactory) {
                    consumer.accept(dsBw);
                }
            });
        }
    }
}
