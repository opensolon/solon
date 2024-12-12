package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.List;

public class SqlUpdateExpression implements ISqlUpdateExpression {
    protected final ISqlFromExpression from;
    protected final ISqlJoinsExpression joins;
    protected final ISqlSetsExpression sets;
    protected final ISqlWhereExpression where;

    public SqlUpdateExpression(ISqlFromExpression from, ISqlJoinsExpression joins, ISqlSetsExpression sets, ISqlWhereExpression where) {
        this.from = from;
        this.joins = joins;
        this.sets = sets;
        this.where = where;
    }

    @Override
    public ISqlFromExpression getFrom() {
        return from;
    }

    @Override
    public ISqlJoinsExpression getJoins() {
        return joins;
    }

    @Override
    public ISqlSetsExpression getSets() {
        return sets;
    }

    @Override
    public ISqlWhereExpression getWhere() {
        return where;
    }

    @Override
    public void addJoin(ISqlJoinExpression join) {
        joins.addJoin(join);
    }

    @Override
    public void addSet(ISqlSetExpression set) {
        sets.addSet(set);
    }

    @Override
    public void addWhere(ISqlExpression where) {
        this.where.addCondition(where);
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        List<String> strings=new ArrayList<>();
        strings.add("UPDATE");
        strings.add(from.getTableName());
        strings.add("AS");
        strings.add(from.getAsName());
        if (!joins.isEmpty()) {
            strings.add(joins.getSqlAndValue(config, values));
        }
        strings.add(sets.getSqlAndValue(config, values));
        if (!where.isEmpty()) {
            strings.add(where.getSqlAndValue(config, values));
        }
        return String.join(" ", strings);
    }
}
