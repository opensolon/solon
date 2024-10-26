package org.noear.solon.data.sqlink.core.api.crud.update;

import org.noear.solon.data.sqlink.core.api.crud.CRUD;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.base.session.SqlSession;
import org.noear.solon.data.sqlink.core.visitor.NormalVisitor;
import org.noear.solon.data.sqlink.core.visitor.SetVisitor;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.LambdaExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UpdateBase extends CRUD
{
    public final static Logger log = LoggerFactory.getLogger(UpdateBase.class);

    private final UpdateSqlBuilder sqlBuilder;

    public UpdateBase(IConfig config, Class<?> target)
    {
        this.sqlBuilder = new UpdateSqlBuilder(config,target);
    }

    public UpdateBase(UpdateSqlBuilder sqlBuilder)
    {
        this.sqlBuilder = sqlBuilder;
    }

    protected UpdateSqlBuilder getSqlBuilder()
    {
        return sqlBuilder;
    }

    protected IConfig getConfig()
    {
        return sqlBuilder.getConfig();
    }

    public String toSql()
    {
        return sqlBuilder.getSql();
    }

    public long executeRows()
    {
        IConfig config = getConfig();
        checkHasWhere();
        List<Object> values = new ArrayList<>();
        String sql = sqlBuilder.getSqlAndValue(values);
        tryPrintUseDs(log, config.getDataSourceManager().getDsKey());
        tryPrintSql(log, sql);
        SqlSession session = config.getSqlSessionFactory().getSession();
        return session.executeUpdate(sql, values);
    }

    private void checkHasWhere()
    {
        if (getConfig().isIgnoreUpdateNoWhere()) return;
        if (!sqlBuilder.hasWhere())
        {
            throw new RuntimeException("UPDATE没有条件");
        }
    }

    protected void join(JoinType joinType, Class<?> target, ExprTree<?> expr)
    {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression on = normalVisitor.visit(expr.getTree());
        ISqlTableExpression table = factory.table(target);
        getSqlBuilder().addJoin(target, joinType, table, on);
    }

    protected void set(LambdaExpression<?> lambda)
    {
        SetVisitor setVisitor = new SetVisitor(getConfig());
        ISqlExpression expression = setVisitor.visit(lambda);
        if (expression instanceof ISqlSetsExpression)
        {
            ISqlSetsExpression sqlSetsExpression = (ISqlSetsExpression) expression;
            sqlBuilder.addSet(sqlSetsExpression);
        }
        else if (expression instanceof ISqlSetExpression)
        {
            ISqlSetExpression sqlSetExpression = (ISqlSetExpression) expression;
            sqlBuilder.addSet(sqlSetExpression);
        }
    }

    protected void where(LambdaExpression<?> lambda)
    {
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression expression = normalVisitor.visit(lambda);
        sqlBuilder.addWhere(expression);
    }
}
