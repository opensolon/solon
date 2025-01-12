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
package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlLimitExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlLimitExpression implements ISqlLimitExpression {
    protected long offset, rows;

    public long getOffset() {
        return offset;
    }

    public long getRows() {
        return rows;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setRows(long rows) {
        this.rows = rows;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        if (getRows() > 0) {
            if (getOffset() > 0) {
                return String.format("LIMIT %d OFFSET %d", getRows(), getOffset());
            }
            else {
                return String.format("LIMIT %d", getRows());
            }
        }
        return "";
    }
}
