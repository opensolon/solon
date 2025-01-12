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

import org.noear.solon.data.sqlink.base.DbType;

import java.lang.annotation.*;

/**
 * SQL扩展表达式注解
 *
 * @author kiryu1223
 * @since 3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(SqlExtensionExpressions.class)
public @interface SqlExtensionExpression {
    /**
     * 数据库类型
     */
    DbType dbType() default DbType.Any;

    /**
     * 字符串模板，占位符为{参数名1}，{参数名2}等，优先级小于extension
     */
    String template();

    /**
     * 可变参数下用于分割参数的字符串
     */
    String separator() default ",";

    /**
     * 动态扩展表达式类型，优先级大于template
     */
    Class<? extends BaseSqlExtension> extension() default BaseSqlExtension.class;
}
