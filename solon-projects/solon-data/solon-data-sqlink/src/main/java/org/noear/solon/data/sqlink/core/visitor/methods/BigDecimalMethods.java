package org.noear.solon.data.sqlink.core.visitor.methods;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;

import java.util.Arrays;
import java.util.List;

public class BigDecimalMethods
{
    public static ISqlExpression remainder(IConfig config, ISqlExpression left, ISqlExpression right)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        List<ISqlExpression> sqlExpressions;
        switch (config.getDbType())
        {
            case Oracle:
                function = Arrays.asList("MOD(", ",", ")");
                break;
            default:
                function = Arrays.asList("(", " % ", ")");
        }
        return factory.template(function, Arrays.asList(left, right));
    }
}
