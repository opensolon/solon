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
package org.noear.solon.data.rx.sql;


import org.noear.solon.core.util.RankEntity;
import org.noear.solon.data.rx.sql.intercept.RxSqlExecuteInterceptor;
import org.noear.solon.data.rx.sql.intercept.RxSqlExecutor;
import org.noear.solon.data.rx.sql.intercept.RxSqlExecutorWrapper;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sql 配置类
 *
 * @author noear
 * @since 3.0
 */
public class RxSqlConfiguration {
    private static List<RankEntity<RxSqlExecuteInterceptor>> interceptorList = new ArrayList<>();

    /**
     * 设置执行拦截器
     */
    public static void addInterceptor(RxSqlExecuteInterceptor interceptor, int index) {
        interceptorList.add(new RankEntity<>(interceptor, index));
        Collections.sort(interceptorList);
    }

    /**
     * 执行拦截
     *
     * @param cmd     命令
     * @param excutor 执行器
     */
    public static Publisher doIntercept(RxSqlCommand cmd, RxSqlExecutor excutor) {
        return new RxSqlExecutorWrapper(interceptorList, excutor).execute(cmd);
    }
}