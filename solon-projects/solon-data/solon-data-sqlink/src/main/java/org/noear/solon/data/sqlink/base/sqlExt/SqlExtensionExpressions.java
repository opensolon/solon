package org.noear.solon.data.sqlink.base.sqlExt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SqlExtensionExpressions
{
    SqlExtensionExpression[] value();
}
