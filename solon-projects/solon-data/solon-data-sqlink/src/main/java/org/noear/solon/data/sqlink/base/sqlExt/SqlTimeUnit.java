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
package org.noear.solon.data.sqlink.base.sqlExt;

import org.noear.solon.data.sqlink.base.SqLinkConfig;

/**
 * SQL时间单位
 *
 * @author kiryu1223
 * @since 3.0
 */
public enum SqlTimeUnit implements ISqlKeywords {
    /**
     * 年
     */
    YEAR,
    /**
     * 月
     */
    MONTH,
    /**
     * 周
     */
    WEEK,
    /**
     * 天
     */
    DAY,
    /**
     * 小时
     */
    HOUR,
    /**
     * 分钟
     */
    MINUTE,
    /**
     * 秒
     */
    SECOND,
    /**
     * 毫秒
     */
    MILLISECOND,
    /**
     * 微秒
     */
    MICROSECOND,
    /**
     * 纳秒
     */
    NANOSECOND,
    ;

    @Override
    public String getKeyword(SqLinkConfig config) {
        return name();
    }
}
