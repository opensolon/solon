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
package org.noear.solon.data.sql.integration;

import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.VarHolder;
import org.noear.solon.data.datasource.DsUtils;
import org.noear.solon.data.sql.SqlConfiguration;
import org.noear.solon.data.sql.SqlUtils;
import org.noear.solon.data.sql.bound.RowConverterFactory;
import org.noear.solon.data.sql.intercept.SqlCommandInterceptor;

/**
 * @author noear
 * @since 3.0
 */
public class SqlUtilsPlugin implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        context.beanInjectorAdd(Inject.class, SqlUtils.class, this::doInject);

        context.getBeanAsync(RowConverterFactory.class, bean -> {
            SqlConfiguration.setConverter(bean);
        });

        context.getWrapAsync(SqlCommandInterceptor.class, bw -> {
            SqlConfiguration.addInterceptor(bw.raw(), bw.index());
        });
    }

    void doInject(VarHolder vh, Inject anno) {
        vh.required(anno.required());

        DsUtils.observeDs(vh.context(), anno.value(), bw -> {
            vh.setValue(SqlUtils.of(bw.raw()));
        });
    }
}
