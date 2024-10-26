package org.noear.solon.data.sqlink.core.exception;

import io.github.kiryu1223.expressionTree.expressions.Expression;

public class IllegalExpressionException extends RuntimeException
{
    public IllegalExpressionException(Expression expression)
    {
        super("Unsupported expression\n" + expression);
    }
}
