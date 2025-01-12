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
package org.noear.solon.data.sqlink.core.exception;

import org.noear.solon.data.sqlink.base.DbType;

/**
 * 时间运算异常
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SqLinkIntervalException extends SqLinkException {
    public SqLinkIntervalException(DbType type) {
        super(type.name() + "下的date加减运算函数必须为字面量或者java引用（不可以是数据库字段引用）");
    }
}
