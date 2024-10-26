package org.noear.solon.data.sqlink.core.api.crud.delete;

import org.noear.solon.data.sqlink.core.api.crud.CRUD;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;
import org.noear.solon.data.sqlink.core.sqlBuilder.DeleteSqlBuilder;
import org.noear.solon.data.sqlink.base.session.SqlSession;
import org.noear.solon.data.sqlink.core.visitor.NormalVisitor;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.LambdaExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class DeleteBase extends CRUD
{
    public final static Logger log = LoggerFactory.getLogger(DeleteBase.class);

    private final DeleteSqlBuilder sqlBuilder;

    public DeleteBase(IConfig config, Class<?> target)
    {
        this.sqlBuilder = new DeleteSqlBuilder(config,target);
    }

    public DeleteBase(DeleteSqlBuilder sqlBuilder)
    {
        this.sqlBuilder = sqlBuilder;
    }

    protected DeleteSqlBuilder getSqlBuilder()
    {
        return sqlBuilder;
    }

    public IConfig getConfig()
    {
        return sqlBuilder.getConfig();
    }

    public long executeRows()
    {
        IConfig config = getConfig();
        checkHasWhere();
        List<Object> values = new ArrayList<>();
        String sql = sqlBuilder.getSqlAndValue(values);
        tryPrintUseDs(log,config.getDataSourceManager().getDsKey());
        tryPrintSql(log, sql);
        SqlSession session = config.getSqlSessionFactory().getSession();
        return session.executeUpdate(sql, values);
    }

    public String toSql()
    {
        return sqlBuilder.getSql();
    }

    private void checkHasWhere()
    {
        if (getConfig().isIgnoreDeleteNoWhere()) return;
        if (!sqlBuilder.hasWhere())
        {
            throw new RuntimeException("DELETE没有条件");
        }
    }

    protected void join(JoinType joinType, Class<?> target, ExprTree<?> expr)
    {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression on = normalVisitor.visit(expr.getTree());
        getSqlBuilder().addJoin(target, joinType, factory.table(target), on);
    }

    protected void selectDeleteTable(Class<?> c)
    {
        getSqlBuilder().addExclude(c);
    }

    protected void where(LambdaExpression<?> lambda)
    {
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression expression = normalVisitor.visit(lambda);
        sqlBuilder.addWhere(expression);
    }
}
