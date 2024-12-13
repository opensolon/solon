package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.SqLinkConfig;

import java.util.List;

public interface ISqlWithsExpression extends ISqlExpression {
    List<ISqlWithExpression> getWiths();

    void addWith(ISqlWithExpression withExpression);

    @Override
    default ISqlWithsExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlWithsExpression withs = factory.withs();
        for (ISqlWithExpression with : getWiths()) {
            withs.addWith(with);
        }
        return withs;
    }
}
