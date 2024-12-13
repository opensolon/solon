package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlWithExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlWithsExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SqlWithsExpression implements ISqlWithsExpression {
    protected final List<ISqlWithExpression> withExpressions = new ArrayList<>();

    @Override
    public List<ISqlWithExpression> getWiths() {
        return withExpressions;
    }

    @Override
    public void addWith(ISqlWithExpression withExpression) {
        withExpressions.add(withExpression);
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        if (withExpressions.isEmpty()) return "";
        List<String> strings = new ArrayList<>(withExpressions.size());
        for (ISqlWithExpression withExpression : withExpressions) {
            strings.add(withExpression.getSqlAndValue(config, values));
        }
        return "WITH " + String.join(",", strings);
    }
}
