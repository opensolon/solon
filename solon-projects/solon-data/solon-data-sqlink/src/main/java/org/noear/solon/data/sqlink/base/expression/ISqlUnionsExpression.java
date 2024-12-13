package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.SqLinkConfig;

import java.util.List;

public interface ISqlUnionsExpression extends ISqlExpression {
    List<ISqlUnionExpression> getUnions();

    void addUnion(ISqlUnionExpression union);

    @Override
    default ISqlUnionsExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlUnionsExpression unions = factory.unions();
        for (ISqlUnionExpression union : getUnions()) {
            unions.addUnion(union.copy(config));
        }
        return unions;
    }
}
