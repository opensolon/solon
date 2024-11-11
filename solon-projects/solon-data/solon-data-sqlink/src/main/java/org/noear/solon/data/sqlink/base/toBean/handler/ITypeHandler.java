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
package org.noear.solon.data.sqlink.base.toBean.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类型处理器
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ITypeHandler<T> {
    /**
     * 获取值
     *
     * @param resultSet 结果集
     * @param index     索引
     * @param type      类型
     */
    T getValue(ResultSet resultSet, int index, Type type) throws SQLException;

    /**
     * 设置值
     *
     * @param preparedStatement 预处理语句
     * @param index             索引
     * @param value             值
     */
    void setValue(PreparedStatement preparedStatement, int index, T value) throws SQLException;

    /**
     * 转换字符串到对应的值
     *
     * @param value 字符串值
     * @param type 对应的类型
     */
    T castStringToTarget(String value, Type type);

    /**
     * 获取泛型类型
     */
    default Type getGenericType() {
        Type[] genericInterfaces = this.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType anInterface = (ParameterizedType) genericInterface;
                if (anInterface.getRawType() == ITypeHandler.class) {
                    return anInterface.getActualTypeArguments()[0];
                }
            }
        }
        throw new RuntimeException("未知的ITypeHandler<T>实现类:" + this.getClass());
    }
}
