package org.noear.solon.data.sqlink.base.sqlExt;

import org.noear.solon.data.sqlink.base.DbType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(SqlExtensionExpressions.class)
public @interface SqlExtensionExpression
{
    DbType dbType() default DbType.Any;

    String template();

    String separator() default ",";

    Class<? extends BaseSqlExtension> extension() default BaseSqlExtension.class;
}
