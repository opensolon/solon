package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlUnionExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlUnionsExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.List;

public class SqlUnionsExpression implements ISqlUnionsExpression {

    protected final List<ISqlUnionExpression> unions = new ArrayList<>();


    @Override
    public List<ISqlUnionExpression> getUnions() {
        return unions;
    }

    @Override
    public void addUnion(ISqlUnionExpression union) {
        unions.add(union);
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        if (unions.isEmpty()) return "";
        List<String> strings = new ArrayList<>(unions.size());
        for (ISqlUnionExpression union : unions) {
            strings.add(union.getSqlAndValue(config, values));
        }
//        for (int i = unions.size() - 1; i >= 0; i--) {
//            ISqlUnionExpression union = unions.get(i);
//            strings.add(union.getSqlAndValue(config, values));
//        }
        return String.join(" ", strings);
    }
}
