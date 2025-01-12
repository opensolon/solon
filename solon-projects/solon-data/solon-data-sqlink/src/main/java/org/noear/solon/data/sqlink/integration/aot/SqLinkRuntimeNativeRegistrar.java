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
package org.noear.solon.data.sqlink.integration.aot;

import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AppContext;
import org.noear.solon.data.sqlink.api.crud.read.group.*;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.datetime.*;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.number.*;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.other.URLTypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.varchar.CharTypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.varchar.StringTypeHandler;
import org.noear.solon.data.sqlink.api.crud.read.Empty;
import org.noear.solon.data.sqlink.core.sqlExt.types.Char;
import org.noear.solon.data.sqlink.core.sqlExt.types.SqlTypes;
import org.noear.solon.data.sqlink.core.sqlExt.types.Varchar;
import org.noear.solon.data.sqlink.integration.configuration.SqLinkProperties;

import java.util.Arrays;
import java.util.List;

/**
 * solon环境下的aot配置
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SqLinkRuntimeNativeRegistrar implements RuntimeNativeRegistrar {
    @Override
    public void register(AppContext context, RuntimeNativeMetadata metadata) {
        //配置文件
        metadata.registerReflection(SqLinkProperties.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        //空表
        metadata.registerReflection(Empty.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        //Group类
        metadata.registerReflection(Group.class, MemberCategory.PUBLIC_FIELDS);
        metadata.registerReflection(Group2.class, MemberCategory.PUBLIC_FIELDS);
        metadata.registerReflection(Group3.class, MemberCategory.PUBLIC_FIELDS);
        metadata.registerReflection(Group4.class, MemberCategory.PUBLIC_FIELDS);
        metadata.registerReflection(Group5.class, MemberCategory.PUBLIC_FIELDS);
        metadata.registerReflection(Group6.class, MemberCategory.PUBLIC_FIELDS);
        metadata.registerReflection(Group7.class, MemberCategory.PUBLIC_FIELDS);
        metadata.registerReflection(Group8.class, MemberCategory.PUBLIC_FIELDS);
        metadata.registerReflection(Group9.class, MemberCategory.PUBLIC_FIELDS);
        metadata.registerReflection(Group10.class, MemberCategory.PUBLIC_FIELDS);
        //Group的聚合函数类
        metadata.registerReflection(SqlAggregation.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerReflection(SqlAggregation2.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerReflection(SqlAggregation3.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerReflection(SqlAggregation4.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerReflection(SqlAggregation5.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerReflection(SqlAggregation6.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerReflection(SqlAggregation7.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerReflection(SqlAggregation8.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerReflection(SqlAggregation9.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerReflection(SqlAggregation10.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        //表达式扩展
//        registerExtension(metadata, Arrays.asList(
//                MySqlDateTimeDiffExtension.class,
//                OracleAddOrSubDateExtension.class,
//                OracleCastExtension.class,
//                OracleDateTimeDiffExtension.class,
//                OracleJoinExtension.class
//        ));
        //sql类型
        registerSqlType(metadata, Arrays.asList(
                Char.class,
                Varchar.class
        ));
        // typeHandler
        registerTypeHandler(metadata, Arrays.asList(
                ITypeHandler.class,
                CharTypeHandler.class,
                StringTypeHandler.class,
                ByteTypeHandler.class,
                ShortTypeHandler.class,
                IntTypeHandler.class,
                LongTypeHandler.class,
                BoolTypeHandler.class,
                FloatTypeHandler.class,
                DoubleTypeHandler.class,
                BigIntegerTypeHandler.class,
                BigDecimalTypeHandler.class,
                DateTypeHandler.class,
                UtilDateHandler.class,
                TimeTypeHandler.class,
                TimestampTypeHandler.class,
                LocalDateTimeTypeHandler.class,
                LocalDateTypeHandler.class,
                LocalTimeTypeHandler.class,
                URLTypeHandler.class
        ));
    }

//    private void registerExtension(RuntimeNativeMetadata metadata, List<Class<? extends BaseSqlExtension>> extensions)
//    {
//        for (Class<? extends BaseSqlExtension> extension : extensions)
//        {
//            metadata.registerReflection(extension, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
//        }
//    }

    private void registerSqlType(RuntimeNativeMetadata metadata, List<Class<? extends SqlTypes<?>>> sqlTypes) {
        for (Class<? extends SqlTypes<?>> sqlType : sqlTypes) {
            metadata.registerReflection(sqlType, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
        }
    }

    private void registerTypeHandler(RuntimeNativeMetadata metadata, List<Class<? extends ITypeHandler>> typeHandlers) {
        for (Class<? extends ITypeHandler> typeHandler : typeHandlers) {
            metadata.registerReflection(typeHandler, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
        }
    }
}
