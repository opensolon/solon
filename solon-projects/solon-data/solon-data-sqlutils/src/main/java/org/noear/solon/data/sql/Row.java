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
package org.noear.solon.data.sql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 行
 *
 * @author noear
 * @since 3.0
 */
public interface Row {
    /**
     * 行大小
     */
    int size();

    /**
     * 元信息
     */
    ResultSetMetaData meta();

    /**
     * 行数据
     */
    Object[] data();

    /**
     * 获取名字
     *
     * @param columnIdx 列顺位（从1开始）
     */
    String getName(int columnIdx) throws SQLException;

    /**
     * 获取名字列顺位
     *
     * @param name 名字
     */
    int getNameColumnIdx(String name) throws SQLException;

    /**
     * 获取值
     *
     * @param columnIdx 列顺位（从1开始）
     */
    Object getValue(int columnIdx) throws SQLException;

    /**
     * 获取值
     *
     * @param name 名字
     */
    Object getValue(String name) throws SQLException;

    /**
     * 转为 Map
     */
    default Map<String, Object> toMap() throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int cI = 1; cI <= size(); cI++) {
            map.put(getName(cI), getValue(cI));
        }

        return map;
    }

    /**
     * 转为 Bean
     */
    <T> T toBean(Class<?> clazz) throws SQLException;
}