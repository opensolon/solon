package org.noear.solon.data.sqlink.plugin.aot;


import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AppContext;
import org.noear.solon.data.sqlink.base.metaData.NoConverter;
import org.noear.solon.data.sqlink.core.api.crud.read.Empty;
import org.noear.solon.data.sqlink.core.api.crud.read.group.*;
import org.noear.solon.data.sqlink.core.sqlExt.types.Char;
import org.noear.solon.data.sqlink.core.sqlExt.types.SqlTypes;
import org.noear.solon.data.sqlink.core.sqlExt.types.Varchar2;
import org.noear.solon.data.sqlink.plugin.configuration.SQLinkProperties;

import java.util.Arrays;
import java.util.List;


public class SQLinkRuntimeNativeRegistrar implements RuntimeNativeRegistrar
{
    @Override
    public void register(AppContext context, RuntimeNativeMetadata metadata)
    {
        //配置文件
        metadata.registerReflection(SQLinkProperties.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        //转换器
        metadata.registerReflection(NoConverter.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
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
                Varchar2.class
        ));
    }

//    private void registerExtension(RuntimeNativeMetadata metadata, List<Class<? extends BaseSqlExtension>> extensions)
//    {
//        for (Class<? extends BaseSqlExtension> extension : extensions)
//        {
//            metadata.registerReflection(extension, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
//        }
//    }

    private void registerSqlType(RuntimeNativeMetadata metadata, List<Class<? extends SqlTypes<?>>> sqlTypes)
    {
        for (Class<? extends SqlTypes<?>> sqlType : sqlTypes)
        {
            metadata.registerReflection(sqlType, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
        }
    }
}
