package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.api.crud.read.TreeCte;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.List;

public class SqlRecursionExpression implements ISqlRecursionExpression {
    protected final ISqlQueryableExpression queryable;
    protected final String parentId;
    protected final String childId;
    protected final int level;

    public SqlRecursionExpression(ISqlQueryableExpression queryable, String parentId, String childId, int level) {
        this.queryable = queryable;
        this.parentId = parentId;
        this.childId = childId;
        this.level = level;
    }

    @Override
    public ISqlQueryableExpression getQueryable() {
        return queryable;
    }

    @Override
    public String recursionKeyword() {
        return "";
    }

    @Override
    public String withTableName() {
        MetaData treeCteMetaData = MetaDataCache.getMetaData(TreeCte.class);
        return treeCteMetaData.getTableName();
    }

    @Override
    public String parentId() {
        return parentId;
    }

    @Override
    public String childId() {
        return childId;
    }

    @Override
    public int level() {
        return level;
    }
    // WITH "as_tree_cte" as (
    // SELECT 0 as cte_level, a."Code", a."Name", a."ParentCode"
    // FROM "Area" a
    // WHERE (a."Name" = '中国')

    // union all

    // SELECT wct1.cte_level + 1 as cte_level, wct2."Code", wct2."Name", wct2."ParentCode"
    // FROM "as_tree_cte" wct1
    // INNER JOIN "Area" wct2 ON wct2."ParentCode" = wct1."Code"
    // )

    // SELECT a."Code", a."Name", a."ParentCode"
    // FROM "as_tree_cte" a
    // ORDER BY a."Code"

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        SqLinkDialect dialect = config.getDisambiguation();
        ISqlQueryableExpression queryCopy = queryable.copy(config);
        ISqlSelectExpression selectCopy = queryCopy.getSelect().copy(config);
        Class<?> mainTableClass = queryCopy.getMainTableClass();
        // SELECT ..., 0 as cte_level
        for (ISqlExpression column : selectCopy.getColumns()) {
            if (column instanceof ISqlColumnExpression) {
                ISqlColumnExpression iSqlColumnExpression = (ISqlColumnExpression) column;
                iSqlColumnExpression.setTableAsName("wct2");
            }
        }
        // SELECT ..., wct1.cte_level + 1 as cte_level
        ISqlQueryableExpression wct = factory.queryable(selectCopy, factory.from(factory.table(TreeCte.class), "wct1"));
        wct.addJoin(factory.join(JoinType.INNER, factory.table(mainTableClass), factory.binary(SqlOperator.EQ, factory.constString(dialect.disambiguation("wct2")+"." + parentId), factory.constString(dialect.disambiguation("wct1")+"." + childId)), "wct2"));
        tryLevel(config, queryCopy.getSelect(), wct.getSelect(), wct.getWhere());
        List<String> templates = new ArrayList<>();
        String s = recursionKeyword();
        if (!s.isEmpty()) {
            templates.add(s);
        }
        templates.add(dialect.disambiguationTableName(withTableName()));
        templates.add("AS");
        templates.add("(" + queryCopy.getSqlAndValue(config, values));
        templates.add("UNION ALL");
        templates.add(wct.getSqlAndValue(config, values) + ")");
        return String.join(" ", templates);
    }

    protected void tryLevel(SqLinkConfig config, ISqlSelectExpression s1, ISqlSelectExpression s2, ISqlWhereExpression where) {
        if (level > 0) {
            SqlExpressionFactory factory = config.getSqlExpressionFactory();
            SqLinkDialect dialect = config.getDisambiguation();
            s1.addColumn(factory.as(factory.constString("0"), "cte_level"));
            ISqlConstStringExpression wct1_cte_level = factory.constString(dialect.disambiguation("wct1") + "." + dialect.disambiguation("cte_level"));
            s2.addColumn(factory.as(factory.binary(SqlOperator.PLUS,wct1_cte_level,factory.constString("1")), "cte_level"));
            where.addCondition(factory.binary(SqlOperator.LT, wct1_cte_level, factory.constString(String.valueOf(level))));
        }
    }
}
