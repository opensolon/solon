package org.noear.solon.data.sqlink.core.visitor;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlColumnExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;
import org.noear.solon.data.sqlink.core.visitor.methods.LogicExpression;
import io.github.kiryu1223.expressionTree.expressions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.isBool;
import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.isGroupKey;

public class SelectVisitor extends SqlVisitor
{
    private final ISqlQueryableExpression queryable;
    //private boolean useUnNameClassOrNotFirst = false;

    public SelectVisitor(IConfig config, ISqlQueryableExpression queryable)
    {
        super(config);
        this.queryable = queryable;
    }

    @Override
    public ISqlExpression visit(NewExpression newExpression)
    {
        BlockExpression classBody = newExpression.getClassBody();
        if (classBody == null)
        {
            return super.visit(newExpression);
        }
        else
        {
            //useUnNameClassOrNotFirst = true;
            List<ISqlExpression> expressions = new ArrayList<>();
            for (Expression expression : classBody.getExpressions())
            {
                if (expression.getKind() == Kind.Variable)
                {
                    VariableExpression variable = (VariableExpression) expression;
                    String name = variable.getName();
                    MetaData metaData = MetaDataCache.getMetaData(newExpression.getType());
                    Expression init = variable.getInit();
                    if (init != null)
                    {
                        ISqlExpression context = visit(variable.getInit());
                        context = boxTheBool(variable.getInit(), context);
                        setAs(expressions, context, name);
                    }
                }
            }
            return factory.select(expressions, newExpression.getType());
        }
    }

//    @Override
//    public SqlExpression visit(MethodCallExpression methodCall)
//    {
//        SqlExpression visit = super.visit(methodCall);
//        if (useUnNameClassOrNotFirst)
//        {
//            return visit;
//        }
//        else
//        {
//            useUnNameClassOrNotFirst=true;
//            return boxTheBool(methodCall, visit);
//        }
//    }
//
//    @Override
//    public SqlExpression visit(UnaryExpression unary)
//    {
//        SqlExpression visit = super.visit(unary);
//        if (useUnNameClassOrNotFirst)
//        {
//            return visit;
//        }
//        else
//        {
//            useUnNameClassOrNotFirst=true;
//            return boxTheBool(unary, visit);
//        }
//    }

    //    @Override
//    public SqlExpression visit(BlockExpression blockExpression)
//    {
//        List<SqlExpression> expressions = new ArrayList<>();
//        for (Expression expression : blockExpression.getExpressions())
//        {
//            if (expression instanceof VariableExpression)
//            {
//                VariableExpression variableExpression = (VariableExpression) expression;
//                if (cur == null)
//                {
//                    cur = variableExpression.getParameter();
//                }
//                else
//                {
//                    throw new IllegalExpressionException(blockExpression);
//                }
//            }
//            else if (expression instanceof MethodCallExpression)
//            {
//                MethodCallExpression methodCall = (MethodCallExpression) expression;
//                Method method = methodCall.getMethod();
//                if (isSetter(method) && methodCall.getExpr() == cur)
//                {
//                    MetaData metaData = MetaDataCache.getMetaData(method.getDeclaringClass());
//                    String name = metaData.getColumnNameBySetter(method);
//                    //propertyMetaData.add(metaData.getPropertyMetaDataByFieldName(name));
//                    SqlExpression expr = visit(methodCall.getArgs().get(0));
//                    setAs(expressions, expr, name);
//                }
//            }
//        }
//        return factory.select(expressions,cur.getType());
//    }

    @Override
    public ISqlExpression visit(FieldSelectExpression fieldSelect)
    {
        if (isGroupKey(parameters, fieldSelect)) // g.key
        {
            return queryable.getGroupBy().getColumns().get("key");
        }
        else if (isGroupKey(parameters, fieldSelect.getExpr())) // g.key.xxx
        {
            Map<String, ISqlExpression> columns = queryable.getGroupBy().getColumns();
            return columns.get(fieldSelect.getField().getName());
        }
        else
        {
            return super.visit(fieldSelect);
        }
    }

    @Override
    protected SqlVisitor getSelf()
    {
        return new SelectVisitor(config, queryable);
    }

    @Override
    public ISqlExpression visit(ParameterExpression parameter)
    {
        if (parameters.contains(parameter))
        {
            Class<?> type = parameter.getType();
            int index = parameters.indexOf(parameter);
            MetaData metaData = MetaDataCache.getMetaData(type);
            //propertyMetaData.addAll(metaData.getColumns().values());
            List<ISqlExpression> expressions = new ArrayList<>();
            for (PropertyMetaData pm : metaData.getNotIgnorePropertys())
            {
                expressions.add(factory.column(pm, index));
            }
            return factory.select(expressions, parameter.getType());
        }
        else
        {
            return super.visit(parameter);
        }
    }

    protected void setAs(List<ISqlExpression> contexts, ISqlExpression expression, String name)
    {
        if (expression instanceof ISqlColumnExpression)
        {
            ISqlColumnExpression sqlColumn = (ISqlColumnExpression) expression;
            if (!sqlColumn.getPropertyMetaData().getColumn().equals(name))
            {
                contexts.add(factory.as(expression, name));
            }
            else
            {
                contexts.add(expression);
            }
        }
        else
        {
            contexts.add(factory.as(expression, name));
        }
    }

    protected ISqlExpression boxTheBool(Expression init, ISqlExpression result)
    {
        if (init instanceof MethodCallExpression)
        {
            MethodCallExpression methodCall = (MethodCallExpression) init;
            return boxTheBool(isBool(methodCall.getMethod().getReturnType()), result);
        }
        else if (init instanceof UnaryExpression)
        {
            UnaryExpression unary = (UnaryExpression) init;
            return boxTheBool(unary.getOperatorType() == OperatorType.NOT, result);
        }
        return result;
    }


    protected ISqlExpression boxTheBool(boolean condition, ISqlExpression result)
    {
        if (!condition) return result;
        switch (config.getDbType())
        {
            case SQLServer:
            case Oracle:
                return LogicExpression.IfExpression(config, result, factory.constString("1"), factory.constString("0"));
            default:
                return result;
        }
    }
}
