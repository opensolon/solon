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
package org.noear.solon.data.sqlink.core.sqlExt.types;

import org.noear.solon.data.sqlink.base.SqLinkConfig;

/**
 * SQL char类型
 *
 * @author kiryu1223
 * @since 3.0
 */
public class Char extends SqlTypes<Character> {
    private final int length;

    public Char(int length) {
        this.length = length;
    }

    @Override
    public String getKeyword(SqLinkConfig config) {
        switch (config.getDbType()) {
            case MySQL:
                return "CHAR(1)";
            case SQLServer:
                return "NCHAR(1)";
        }
        return String.format("CHAR(%d)", length);
    }
}
