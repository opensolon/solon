package org.noear.solon.data.sqlink.core.visitor.methods;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;
import org.noear.solon.data.sqlink.base.expression.SqlOperator;

public class TemporalMethods
{
    public static ISqlExpression isAfter(IConfig config, ISqlExpression thiz, ISqlExpression that)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.binary(SqlOperator.GT, thiz, that);
//        switch (config.getDbType())
//        {
//            case SQLServer:
//                return factory.template(Arrays.asList("IIF((DATEDIFF_BIG(SECOND,", ",", ") <= 0),1,0)"), Arrays.asList(thiz, that));
//            default:
//                return factory.binary(SqlOperator.GT, thiz, that);
//        }
    }

    public static ISqlExpression isBefore(IConfig config, ISqlExpression thiz, ISqlExpression that)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.binary(SqlOperator.LT, thiz, that);
//        switch (config.getDbType())
//        {
//            case SQLServer:
//                return factory.template(Arrays.asList("IIF((DATEDIFF_BIG(SECOND,", ",", ") >= 0),1,0)"), Arrays.asList(thiz, that));
//            default:
//                return factory.binary(SqlOperator.LT, thiz, that);
//        }
    }

    public static ISqlExpression isEqual(IConfig config, ISqlExpression thiz, ISqlExpression that)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.binary(SqlOperator.EQ, thiz, that);
//        switch (config.getDbType())
//        {
//            case SQLServer:
//                return factory.template(Arrays.asList("IIF((DATEDIFF_BIG(SECOND,", ",", ") = 0),1,0)"), Arrays.asList(thiz, that));
//            default:
//                return factory.binary(SqlOperator.EQ, thiz, that);
//        }
    }
}
