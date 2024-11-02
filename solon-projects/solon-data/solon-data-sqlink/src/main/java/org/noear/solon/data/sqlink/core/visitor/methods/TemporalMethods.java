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
package org.noear.solon.data.sqlink.core.visitor.methods;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;
import org.noear.solon.data.sqlink.base.expression.SqlOperator;

/**
 * @author kiryu1223
 * @since 3.0
 */
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
