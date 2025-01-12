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
package org.noear.solon.data.sqlink.base.toBean.Include;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 * 对象抓取器工厂
 *
 * @author kiryu1223
 * @since 3.0
 */
public abstract class IncludeFactory {
    /**
     * 获取抓取器
     *
     * @param config      配置
     * @param session     会话
     * @param targetClass 目标类
     * @param sources     源对象集合
     * @param includes    抓取信息
     * @param queryable   本次的查询表达式
     */
    public abstract <T> IncludeBuilder<T> getBuilder(SqLinkConfig config, SqlSession session, Class<T> targetClass, Collection<T> sources, List<IncludeSet> includes, ISqlQueryableExpression queryable);
}
