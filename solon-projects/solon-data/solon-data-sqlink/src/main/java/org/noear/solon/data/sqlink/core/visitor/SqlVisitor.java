/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.solon.data.sqlink.core.visitor;

import io.github.kiryu1223.expressionTree.expressions.*;
import org.noear.solon.data.sqlink.api.Result;
import org.noear.solon.data.sqlink.api.crud.read.EndQuery;
import org.noear.solon.data.sqlink.api.crud.read.LQuery;
import org.noear.solon.data.sqlink.api.crud.read.group.Grouper;
import org.noear.solon.data.sqlink.api.crud.read.group.IAggregation;
import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.metaData.NavigateData;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;
import org.noear.solon.data.sqlink.base.sqlExt.SqlExtensionExpression;
import org.noear.solon.data.sqlink.base.sqlExt.SqlOperatorMethod;
import org.noear.solon.data.sqlink.core.SubQuery;
import org.noear.solon.data.sqlink.core.exception.SqLinkException;
import org.noear.solon.data.sqlink.core.exception.SqLinkIllegalExpressionException;
import org.noear.solon.data.sqlink.core.exception.SqlFuncExtNotFoundException;
import org.noear.solon.data.sqlink.core.visitor.methods.*;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.*;

/**
 * 表达式解析器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SqlVisitor extends ResultThrowVisitor<ISqlExpression> {
    protected Map<ParameterExpression, AsName> asNameMap = new HashMap<>();
    protected Deque<AsName> asNameDeque = new ArrayDeque<>();
    protected final SqLinkConfig config;
    protected final SqlExpressionFactory factory;
    protected final ISqlFromExpression fromExpression;
    protected final ISqlJoinsExpression joinsExpression;
    protected final ISqlGroupByExpression groupByExpression;
    protected boolean isFirst = true;
    protected boolean isGroup = false;

    public SqlVisitor(SqLinkConfig config, ISqlQueryableExpression sqlQueryableExpression) {
        this(config, sqlQueryableExpression.getFrom(), sqlQueryableExpression.getJoins(), sqlQueryableExpression.getGroupBy());
    }

    public SqlVisitor(SqLinkConfig config, ISqlUpdateExpression updateExpression) {
        this(config, updateExpression.getFrom(), updateExpression.getJoins(), null);
    }

    public SqlVisitor(SqLinkConfig config, ISqlFromExpression fromExpression, ISqlJoinsExpression joinsExpression) {
        this(config, fromExpression, joinsExpression, null);
    }

    protected SqlVisitor(SqLinkConfig config, ISqlFromExpression fromExpression, ISqlJoinsExpression joinsExpression, ISqlGroupByExpression groupByExpression) {
        this.config = config;
        this.factory = config.getSqlExpressionFactory();
        this.fromExpression = fromExpression;
        this.joinsExpression = joinsExpression;
        this.groupByExpression = groupByExpression;
    }

    /**
     * lambda表达式解析
     */
    @Override
    public ISqlExpression visit(LambdaExpression<?> lambda) {
        List<ParameterExpression> parameters = lambda.getParameters();
        // 是否第一次进入或Group的聚合函数查询
        if (isFirst || isGroup) {
            isFirst = false;
            for (int i = 0; i < parameters.size(); i++) {
                ParameterExpression parameter = parameters.get(i);
                if (i == 0) {
                    asNameMap.put(parameter, fromExpression.getAsName());
                }
                else {
                    asNameMap.put(parameter, joinsExpression.getJoins().get(i - 1).getAsName());
                }
            }
            ISqlExpression visit = visit(lambda.getBody());
            for (ParameterExpression parameter : parameters) {
                asNameMap.remove(parameter);
            }
            return visit;
        }
        // 不是的话说明有子查询
        else {
            for (ParameterExpression parameter : parameters) {
                asNameMap.put(parameter, asNameDeque.peek());
            }
            ISqlExpression visit = visit(lambda.getBody());
            for (ParameterExpression parameter : parameters) {
                asNameMap.remove(parameter);
            }
            return visit;
        }
    }

//    protected String doGetAsName(String as) {
//        return doGetAsName(as, 0);
//    }
//
//    protected String doGetAsName(String as, int offset) {
//        String next = offset == 0 ? as : as + offset;
//        if (asNameSet.contains(next)) {
//            return doGetAsName(as, offset + 1);
//        }
//        else {
//            asNameSet.add(next);
//            return next;
//        }
//    }

    /**
     * 赋值表达式解析
     */
    @Override
    public ISqlExpression visit(AssignExpression assignExpression) {
        ISqlExpression left = visit(assignExpression.getLeft());
        if (left instanceof ISqlColumnExpression) {
            ISqlColumnExpression sqlColumnExpression = (ISqlColumnExpression) left;
            ISqlExpression right = visit(assignExpression.getRight());
            return factory.set(sqlColumnExpression, right);
        }
        throw new SqLinkException("表达式中不能出现非lambda入参为赋值对象的语句");
    }

    /**
     * 字段访问表达式解析
     */
    @Override
    public ISqlExpression visit(FieldSelectExpression fieldSelect) {
        if (isProperty(asNameMap, fieldSelect)) {
            ParameterExpression parameter = (ParameterExpression) fieldSelect.getExpr();
            Field field = fieldSelect.getField();
            MetaData metaData = MetaDataCache.getMetaData(field.getDeclaringClass());
            return factory.column(metaData.getFieldMetaDataByFieldName(field.getName()), asNameMap.get(parameter));
        }
        else if (isGroupKey(asNameMap, fieldSelect.getExpr())) // g.key.xxx
        {
            Map<String, ISqlExpression> columns = groupByExpression.getColumns();
            return columns.get(fieldSelect.getField().getName());
        }
        else {
            return checkAndReturnValue(fieldSelect);
        }
    }

    /**
     * 方法调用表达式解析
     */
    @Override
    public ISqlExpression visit(MethodCallExpression methodCall) {
        // equals
        if (ExpressionUtil.isEquals(methodCall)) {
            return factory.binary(SqlOperator.EQ, visit(methodCall.getExpr()), visit(methodCall.getArgs().get(0)));
        }
        // 分组对象的聚合函数
        else if (IAggregation.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            String name = methodCall.getMethod().getName();
            List<Expression> args = methodCall.getArgs();
            isGroup = true;
            switch (name) {
                case "count":
                    ISqlTemplateExpression count = AggregateMethods.count(config, args.isEmpty() ? null : visit(args.get(0)));
                    isGroup = false;
                    return count;
                case "sum":
                    ISqlTemplateExpression sum = AggregateMethods.sum(config, visit(args.get(0)));
                    isGroup = false;
                    return sum;
                case "avg":
                    ISqlTemplateExpression avg = AggregateMethods.avg(config, visit(args.get(0)));
                    isGroup = false;
                    return avg;
                case "max":
                    ISqlTemplateExpression max = AggregateMethods.max(config, visit(args.get(0)));
                    isGroup = false;
                    return max;
                case "min":
                    ISqlTemplateExpression min = AggregateMethods.min(config, visit(args.get(0)));
                    isGroup = false;
                    return min;
                case "groupConcat":
                    List<ISqlExpression> visit = new ArrayList<>();
                    for (Expression arg : args) {
                        visit.add(visit(arg));
                    }
                    ISqlTemplateExpression iSqlTemplateExpression = AggregateMethods.groupConcat(config, visit);
                    isGroup = false;
                    return iSqlTemplateExpression;
                default:
                    throw new SqLinkException("不支持的聚合函数:" + name);
            }
        }
        // SQL扩展函数
        else if (ExpressionUtil.isSqlExtensionExpressionMethod(methodCall.getMethod())) {
            Method sqlFunction = methodCall.getMethod();
            SqlExtensionExpression sqlFuncExt = getSqlFuncExt(sqlFunction.getAnnotationsByType(SqlExtensionExpression.class));
            List<Expression> args = methodCall.getArgs();
            List<ISqlExpression> expressions = new ArrayList<>(args.size());
            if (sqlFuncExt.extension() != BaseSqlExtension.class) {
                for (Expression arg : args) {
                    expressions.add(visit(arg));
                }
                BaseSqlExtension sqlExtension = BaseSqlExtension.getCache(sqlFuncExt.extension());
                return sqlExtension.parse(config, sqlFunction, expressions);
            }
            else {
                List<String> strings = new ArrayList<>();
                List<Parameter> methodParameters = Arrays.stream(methodCall.getMethod().getParameters()).collect(Collectors.toList());
                ParamMatcher match = match(sqlFuncExt.template());
                List<String> functions = match.remainder;
                List<String> params = match.bracesContent;
                for (int i = 0; i < functions.size(); i++) {
                    strings.add(functions.get(i));
                    if (i < params.size()) {
                        String param = params.get(i);
                        Parameter targetParam = methodParameters.stream()
                                .filter(f -> f.getName().equals(param))
                                .findFirst()
                                .orElseThrow(() -> new SqLinkException("无法在" + sqlFuncExt.template() + "中找到" + param));
                        int index = methodParameters.indexOf(targetParam);

                        // 如果是可变参数
                        if (targetParam.isVarArgs()) {
                            while (index < args.size()) {
                                expressions.add(visit(args.get(index)));
                                if (index < args.size() - 1) strings.add(sqlFuncExt.separator());
                                index++;
                            }
                        }
                        // 正常情况
                        else {
                            expressions.add(visit(args.get(index)));
                        }
                    }
                }
                return factory.template(strings, expressions);
            }
        }
        // SQL运算符
        else if (ExpressionUtil.isSqlOperatorMethod(methodCall.getMethod())) {
            Method method = methodCall.getMethod();
            List<Expression> args = methodCall.getArgs();
            SqlOperatorMethod operatorMethod = method.getAnnotation(SqlOperatorMethod.class);
            SqlOperator operator = operatorMethod.value();
            if (operator == SqlOperator.BETWEEN) {
                ISqlExpression thiz = visit(args.get(0));
                ISqlExpression min = visit(args.get(1));
                ISqlExpression max = visit(args.get(2));
                return factory.binary(SqlOperator.BETWEEN, thiz, factory.binary(SqlOperator.AND, min, max));
            }
            else {
                if (operator.isLeft() || operator == SqlOperator.POSTINC || operator == SqlOperator.POSTDEC) {
                    ISqlExpression visit = visit(args.get(0));
                    if (visit instanceof ISqlQueryableExpression) {
                        visit = factory.unary(operator, visit);
                    }
                    return factory.unary(operator, visit);
                }
                else {
                    ISqlExpression left = visit(methodCall.getArgs().get(0));
                    ISqlExpression right = visit(methodCall.getArgs().get(1));
                    if (left instanceof ISqlQueryableExpression) {
                        left = factory.parens(left);
                    }
                    if (right instanceof ISqlQueryableExpression) {
                        right = factory.parens(right);
                    }
                    return factory.binary(operator, left, right);
                }
            }
        }
        // 子查询发起者
        else if (SubQuery.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            if (method.getName().equals("subQuery")) {
                Expression expression = methodCall.getArgs().get(0);
                ISqlExpression visit = visit(expression);
                // subQuery(a.getItems())
                if (visit instanceof ISqlColumnExpression) {
                    ISqlColumnExpression columnExpression = (ISqlColumnExpression) visit;
                    ISqlQueryableExpression query = columnToQuery(columnExpression);
                    // 在子查询发起的地方压入
                    asNameDeque.push(query.getFrom().getAsName());
                    return query;
                }
                // subQuery(a.getOrder().getItem()...getN())
                else if (visit instanceof ISqlQueryableExpression) {
                    ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) visit;
                    ISqlQueryableExpression query = queryToQuery(queryableExpression, method);
                    // 在子查询发起的地方压入
                    asNameDeque.push(query.getFrom().getAsName());
                    return query;
                }
                else {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
            }
            else {
                return checkAndReturnValue(methodCall);
            }
        }
        // 子查询
        else if (LQuery.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())
                || EndQuery.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            if (method.getName().equals("count")) {

                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression column = null;
                if (!methodCall.getArgs().isEmpty()) {
                    column = visit(methodCall.getArgs().get(0));
                }
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.count(config, column)), long.class));
                // 在终结的地方弹出
                asNameDeque.pop();
                return queryable;
            }
            else if (method.getName().equals("sum")) {

                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression column = visit(methodCall.getArgs().get(0));
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.sum(config, column)), BigDecimal.class));

                return queryable;
            }
            else if (method.getName().equals("avg")) {

                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression column = visit(methodCall.getArgs().get(0));
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.avg(config, column)), BigDecimal.class));
                // 在终结的地方弹出
                asNameDeque.pop();
                return queryable;
            }
            else if (method.getName().equals("min")) {

                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression column = visit(methodCall.getArgs().get(0));
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.min(config, column)), BigDecimal.class));
                // 在终结的地方弹出
                asNameDeque.pop();
                return queryable;
            }
            else if (method.getName().equals("max")) {

                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression column = visit(methodCall.getArgs().get(0));
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.max(config, column)), BigDecimal.class));
                // 在终结的地方弹出
                asNameDeque.pop();
                return queryable;
            }
            else if (method.getName().equals("any")) {
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                List<Expression> args = methodCall.getArgs();
                if (!args.isEmpty()) {
                    Expression expression = args.get(0);
                    ISqlExpression cond = visit(expression);
                    queryable.addWhere(cond);
                }
                queryable.setSelect(factory.select(Collections.singletonList(factory.constString("1")), int.class));
                // 在终结的地方弹出
                asNameDeque.pop();
                return factory.unary(SqlOperator.EXISTS, queryable);
            }
            else if (method.getName().equals("where")) {
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression cond = visit(methodCall.getArgs().get(0));
                queryable.addWhere(cond);
                return queryable;
            }
            else if (method.getName().equals("select")) {
                throw new SqLinkException("过于复杂的表达式:" + methodCall);
            }
            else if (method.getName().equals("endSelect")) {
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression select = visit(methodCall.getArgs().get(0));
                queryable.setSelect(factory.select(Collections.singletonList(select), queryable.getMainTableClass()));
                return factory.queryable(queryable.getSelect(), factory.from(queryable, queryable.getFrom().getAsName()));
            }
            else if (method.getName().equals("distinct")) {

                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                List<Expression> args = methodCall.getArgs();
                if (args.isEmpty()) {
                    queryable.setDistinct(true);
                }
                else {
                    ISqlExpression value = visit(args.get(0));
                    if (value instanceof ISqlSingleValueExpression) {
                        ISqlSingleValueExpression iSqlSingleValueExpression = (ISqlSingleValueExpression) value;
                        queryable.setDistinct((boolean) iSqlSingleValueExpression.getValue());
                    }
                    else {
                        throw new SqLinkException("不支持的表达式:" + methodCall);
                    }
                }
                return queryable;
            }
            else if (method.getName().equals("orderBy")) {

                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                List<Expression> args = methodCall.getArgs();
                ISqlExpression orderByColumn = visit(args.get(0));

                if (args.size() > 1) {
                    ISqlExpression value = visit(args.get(1));
                    if (value instanceof ISqlSingleValueExpression) {
                        ISqlSingleValueExpression iSqlSingleValueExpression = (ISqlSingleValueExpression) value;
                        queryable.addOrder(factory.order(orderByColumn, (boolean) iSqlSingleValueExpression.getValue()));
                    }
                    else {
                        throw new SqLinkException("不支持的表达式:" + methodCall);
                    }
                }
                else {
                    queryable.addOrder(factory.order(orderByColumn));
                }
                return queryable;
            }
            else if (method.getName().equals("groupBy")) {
                throw new SqLinkException("过于复杂的表达式:" + methodCall);
            }
            else if (method.getName().equals("having")) {
                throw new SqLinkException("过于复杂的表达式:" + methodCall);
            }
            else if (method.getName().equals("limit")) {

                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;

                List<Expression> args = methodCall.getArgs();
                if (args.size() == 1) {
                    ISqlExpression rows = visit(args.get(0));
                    if (rows instanceof ISqlSingleValueExpression) {
                        ISqlSingleValueExpression iSqlSingleValueExpression = (ISqlSingleValueExpression) rows;
                        queryable.setLimit(0, (long) iSqlSingleValueExpression.getValue());
                    }
                    else {
                        throw new SqLinkException("不支持的表达式:" + methodCall);
                    }
                }
                else if (args.size() == 2) {
                    ISqlExpression offset = visit(args.get(0));
                    ISqlExpression rows = visit(args.get(1));
                    if (rows instanceof ISqlSingleValueExpression && offset instanceof ISqlSingleValueExpression) {
                        ISqlSingleValueExpression rowsValue = (ISqlSingleValueExpression) rows;
                        ISqlSingleValueExpression offsetValue = (ISqlSingleValueExpression) offset;
                        queryable.setLimit((long) offsetValue.getValue(), (long) rowsValue.getValue());
                    }
                    else {
                        throw new SqLinkException("不支持的表达式:" + methodCall);
                    }
                }
                return queryable;
            }
            else if (method.getName().equals("toList")) {
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                // 在终结的地方弹出
                asNameDeque.pop();
                return visit;
            }
            else {
                return checkAndReturnValue(methodCall);
            }
        }
        // 集合的函数
        else if (Collection.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            if (method.getName().equals("contains")) {
                ISqlExpression left = visit(methodCall.getArgs().get(0));
                ISqlExpression right = visit(methodCall.getExpr());
                return factory.binary(SqlOperator.IN, left, right);
            }
            else if (method.getName().equals("size")) {
                ISqlExpression left = visit(methodCall.getExpr());
                if (left instanceof ISqlColumnExpression) {
                    ISqlColumnExpression columnExpression = (ISqlColumnExpression) left;
                    ISqlQueryableExpression query = columnToQuery(columnExpression);
                    query.setSelect(factory.select(Collections.singletonList(AggregateMethods.count(config, factory.constString("*"))), long.class));
                    return query;
                }
                else {
                    throw new SqLinkException(String.format("意外的sql表达式类型:%s 表达式为:%s", left.getClass(), methodCall));
                }
            }
            else if (method.getName().equals("isEmpty")) {
                ISqlExpression left = visit(methodCall.getExpr());
                if (left instanceof ISqlColumnExpression) {
                    ISqlColumnExpression columnExpression = (ISqlColumnExpression) left;
                    ISqlQueryableExpression query = columnToQuery(columnExpression);
                    query.setSelect(factory.select(Collections.singletonList(factory.constString("1")), int.class));
                    return factory.unary(SqlOperator.NOT, factory.unary(SqlOperator.EXISTS, query));
                }
                else {
                    throw new SqLinkException(String.format("意外的sql表达式类型:%s 表达式为:%s", left.getClass(), methodCall));
                }
            }
//            else if (method.getName().equals("stream")) {
//                流支持的功能太少了，就不写了
//            }
            else {
                return checkAndReturnValue(methodCall);
            }
        }
        // 字符串的函数
        else if (String.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            if (Modifier.isStatic(method.getModifiers())) {
                switch (method.getName()) {
                    case "join": {
                        ISqlExpression delimiter = visit(methodCall.getArgs().get(0));
                        //String.join(CharSequence delimiter, CharSequence... elements)
                        if (method.isVarArgs()) {
                            List<ISqlExpression> args = new ArrayList<>(methodCall.getArgs().size() - 1);
                            for (int i = 1; i < methodCall.getArgs().size(); i++) {
                                args.add(visit(methodCall.getArgs().get(i)));
                            }
                            return StringMethods.joinArray(config, delimiter, args);
                        }
                        else {
                            ISqlExpression elements = visit(methodCall.getArgs().get(1));
                            return StringMethods.joinList(config, delimiter, elements);
                        }
                    }
                    default:
                        return checkAndReturnValue(methodCall);
                }
            }
            else {
                switch (method.getName()) {
                    case "contains": {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return StringMethods.contains(config, left, right);
                    }
                    case "startsWith": {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return StringMethods.startsWith(config, left, right);
                    }
                    case "endsWith": {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return StringMethods.endsWith(config, left, right);
                    }
                    case "length": {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.length(config, left);
                    }
                    case "toUpperCase": {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.toUpperCase(config, left);
                    }
                    case "toLowerCase": {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.toLowerCase(config, left);
                    }
                    case "concat": {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return StringMethods.concat(config, left, right);
                    }
                    case "trim": {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.trim(config, left);
                    }
                    case "isEmpty": {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.isEmpty(config, left);
                    }
                    case "indexOf": {
                        if (method.getParameterTypes()[0] == String.class) {
                            ISqlExpression thisStr = visit(methodCall.getExpr());
                            ISqlExpression subStr = visit(methodCall.getArgs().get(0));
                            if (method.getParameterCount() == 1) {
                                return StringMethods.indexOf(config, thisStr, subStr);
                            }
                            else {
                                ISqlExpression fromIndex = visit(methodCall.getArgs().get(1));
                                return StringMethods.indexOf(config, thisStr, subStr, fromIndex);
                            }
                        }
                    }
                    case "replace": {
                        ISqlExpression thisStr = visit(methodCall.getExpr());
                        ISqlExpression oldStr = visit(methodCall.getArgs().get(0));
                        ISqlExpression newStr = visit(methodCall.getArgs().get(1));
                        return StringMethods.replace(config, thisStr, oldStr, newStr);
                    }
                    case "substring": {
                        ISqlExpression thisStr = visit(methodCall.getExpr());
                        ISqlExpression beginIndex = visit(methodCall.getArgs().get(0));
                        if (method.getParameterCount() == 1) {
                            return StringMethods.substring(config, thisStr, beginIndex);
                        }
                        else {
                            ISqlExpression endIndex = visit(methodCall.getArgs().get(1));
                            return StringMethods.substring(config, thisStr, beginIndex, endIndex);
                        }
                    }
                    default:
                        return checkAndReturnValue(methodCall);
                }
            }
        }
        // Math的函数
        else if (Math.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            switch (method.getName()) {
                case "abs": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("ABS(", ")"), Collections.singletonList(arg));
                }
                case "cos": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("COS(", ")"), Collections.singletonList(arg));
                }
                case "acos": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("ACOS(", ")"), Collections.singletonList(arg));
                }
                case "sin": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("SIN(", ")"), Collections.singletonList(arg));
                }
                case "asin": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("ASIN(", ")"), Collections.singletonList(arg));
                }
                case "tan": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("TAN(", ")"), Collections.singletonList(arg));
                }
                case "atan": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("ATAN(", ")"), Collections.singletonList(arg));
                }
                case "atan2": {
                    ISqlExpression arg1 = visit(methodCall.getArgs().get(0));
                    ISqlExpression arg2 = visit(methodCall.getArgs().get(1));
                    return MathMethods.atan2(config, arg1, arg2);
                }
                case "toDegrees": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.toDegrees(config, arg);
                }
                case "toRadians": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.toRadians(config, arg);
                }
                case "exp": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("EXP(", ")"), Collections.singletonList(arg));
                }
                case "floor": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("FLOOR(", ")"), Collections.singletonList(arg));
                }
                case "log": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.log(config, arg);
                }
                case "log10": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.log10(config, arg);
                }
                case "random": {
                    return MathMethods.random(config);
                }
                case "round": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.round(config, arg);
                }
                case "pow": {
                    ISqlExpression arg1 = visit(methodCall.getArgs().get(0));
                    ISqlExpression arg2 = visit(methodCall.getArgs().get(1));
                    return factory.template(Arrays.asList("POWER(", ",", ")"), Arrays.asList(arg1, arg2));
                }
                case "signum": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("SIGN(", ")"), Collections.singletonList(arg));
                }
                case "sqrt": {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("SQRT(", ")"), Collections.singletonList(arg));
                }
                default:
                    return checkAndReturnValue(methodCall);
            }
        }
        // BigDecimal||BigInteger的函数
        else if (BigDecimal.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())
                || BigInteger.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            switch (method.getName()) {
                case "add": {
                    if (method.getParameterCount() == 1) {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return factory.binary(SqlOperator.PLUS, left, right);
                    }
                }
                case "subtract": {
                    if (method.getParameterCount() == 1) {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return factory.binary(SqlOperator.MINUS, left, right);
                    }
                }
                case "multiply": {
                    if (method.getParameterCount() == 1) {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return factory.binary(SqlOperator.MUL, left, right);
                    }
                }
                case "divide": {
                    if (method.getParameterCount() == 1) {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return factory.binary(SqlOperator.DIV, left, right);
                    }
                }
                case "remainder": {
                    if (method.getParameterCount() == 1) {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return BigNumberMethods.remainder(config, left, right);
                    }
                }
                default:
                    return checkAndReturnValue(methodCall);
            }
        }
        // 时间的函数
        else if (Temporal.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            switch (method.getName()) {
                case "isAfter": {
                    ISqlExpression left = visit(methodCall.getExpr());
                    ISqlExpression right = visit(methodCall.getArgs().get(0));
                    return TemporalMethods.isAfter(config, left, right);
                }
                case "isBefore": {
                    ISqlExpression left = visit(methodCall.getExpr());
                    ISqlExpression right = visit(methodCall.getArgs().get(0));
                    return TemporalMethods.isBefore(config, left, right);
                }
                case "isEqual": {
                    ISqlExpression left = visit(methodCall.getExpr());
                    ISqlExpression right = visit(methodCall.getArgs().get(0));
                    return TemporalMethods.isEqual(config, left, right);
                }
                default:
                    return checkAndReturnValue(methodCall);
            }
        }
        // Objects的函数
        else if (Objects.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            if (method.getName().equals("equals")) {
                List<Expression> args = methodCall.getArgs();
                return factory.binary(SqlOperator.EQ, visit(args.get(0)), visit(args.get(1)));
            }
            else if (method.getName().equals("nonNull")) {
                return LogicExpression.notNullExpression(config, visit(methodCall.getArgs().get(0)));
            }
            else {
                return checkAndReturnValue(methodCall);
            }
        }
        else {
            if (isProperty(asNameMap, methodCall)) {
                if (isGetter(methodCall.getMethod())) {
                    ParameterExpression parameter = (ParameterExpression) methodCall.getExpr();
                    Method getter = methodCall.getMethod();
                    MetaData metaData = MetaDataCache.getMetaData(getter.getDeclaringClass());
                    return factory.column(metaData.getFieldMetaDataByGetter(getter), asNameMap.get(parameter));
                }
                else if (isSetter(methodCall.getMethod())) {
                    ParameterExpression parameter = (ParameterExpression) methodCall.getExpr();
                    Method setter = methodCall.getMethod();
                    MetaData metaData = MetaDataCache.getMetaData(setter.getDeclaringClass());
                    FieldMetaData fieldMetaData = metaData.getFieldMetaDataBySetter(setter);
                    ISqlColumnExpression columnExpression = factory.column(fieldMetaData, asNameMap.get(parameter));
                    ISqlExpression value = visit(methodCall.getArgs().get(0));
                    return factory.set(columnExpression, value);
                }
                else if (isDynamicColumn(methodCall.getMethod())) {
                    ParameterExpression parameter = (ParameterExpression) methodCall.getExpr();
                    Expression expression = methodCall.getArgs().get(0);
                    String columnName = expression.getValue().toString();
                    return factory.dynamicColumn(columnName, asNameMap.get(parameter));
                }
                else {
                    return checkAndReturnValue(methodCall);
                }
            }
            else {
                ISqlExpression left = visit(methodCall.getExpr());
                // if left is A.B()
                if (left instanceof ISqlColumnExpression) {
                    ISqlColumnExpression columnExpression = (ISqlColumnExpression) left;
                    Method method = methodCall.getMethod();
                    if (isGetter(method)) {
                        // A.B() => SELECT ... FROM B WHERE B.ID = A.ID
                        ISqlQueryableExpression changed = columnToQuery(columnExpression);
                        // A.B().C() => SELECT ... FROM C WHERE C.ID = (SELECT B.ID FROM B WHERE B.ID = A.ID)
                        return queryToQuery(changed, method);
                    }
                    else {
                        return checkAndReturnValue(methodCall);
                    }
                }
                // if left is A.B()...N()
                else if (left instanceof ISqlQueryableExpression) {
                    Method method = methodCall.getMethod();
                    if (isGetter(method)) {
                        ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) left;
                        return queryToQuery(queryableExpression, method);
                    }
                    else {
                        return checkAndReturnValue(methodCall);
                    }
                }
                else {
                    return checkAndReturnValue(methodCall);
                }
            }
        }
    }

    /**
     * 二元运算表达式解析
     */
    @Override
    public ISqlExpression visit(BinaryExpression binary) {
        ISqlExpression left = visit(binary.getLeft());
        ISqlExpression right = visit(binary.getRight());
        if (left instanceof ISqlQueryableExpression) {
            left = factory.parens(left);
        }
        if (right instanceof ISqlQueryableExpression) {
            right = factory.parens(right);
        }
        return factory.binary(
                SqlOperator.valueOf(binary.getOperatorType().name()),
                left,
                right
        );
    }

    /**
     * 一元运算表达式解析
     */
    @Override
    public ISqlExpression visit(UnaryExpression unary) {
        return factory.unary(
                SqlOperator.valueOf(unary.getOperatorType().name()),
                visit(unary.getOperand())
        );
    }

    /**
     * 三元运算表达式解析
     */
    @Override
    public ISqlExpression visit(ConditionalExpression conditional) {
        ISqlExpression cond = visit(conditional.getCondition());
        ISqlExpression truePart = visit(conditional.getTruePart());
        ISqlExpression falsePart = visit(conditional.getFalsePart());
        return LogicExpression.IfExpression(config, cond, truePart, falsePart);
    }

    /**
     * 括号表达式解析
     */
    @Override
    public ISqlExpression visit(ParensExpression parens) {
        return factory.parens(visit(parens.getExpr()));
    }

    /**
     * 类表达式解析
     */
    @Override
    public ISqlExpression visit(StaticClassExpression staticClass) {
        return factory.type(staticClass.getType());
    }

    /**
     * 常量表达式解析
     */
    @Override
    public ISqlExpression visit(ConstantExpression constant) {
        return factory.AnyValue(constant.getValue());
    }

    /**
     * 引用表达式解析
     */
    @Override
    public ISqlExpression visit(ReferenceExpression reference) {
        return factory.AnyValue(reference.getValue());
    }

    /**
     * new表达式解析
     */
    @Override
    public ISqlExpression visit(NewExpression newExpression) {
        BlockExpression classBody = newExpression.getClassBody();
        if (classBody == null) {
            return checkAndReturnValue(newExpression);
        }
        else {
            Class<?> type = newExpression.getType();
            // GROUP BY
            if (Grouper.class.isAssignableFrom(type)) {
                LinkedHashMap<String, ISqlExpression> contextMap = new LinkedHashMap<>();
                for (Expression expression : classBody.getExpressions()) {
                    if (expression.getKind() == Kind.Variable) {
                        VariableExpression variableExpression = (VariableExpression) expression;
                        String name = variableExpression.getName();
                        Expression init = variableExpression.getInit();
                        if (init != null) {
                            ISqlExpression sqlExpression = visit(init);
                            contextMap.put(name, sqlExpression);
                        }
                    }
                }
                return factory.groupBy(contextMap);
            }
            // SELECT
            else if (Result.class.isAssignableFrom(type)) {
                List<ISqlExpression> expressions = new ArrayList<>();
                for (Expression expression : classBody.getExpressions()) {
                    if (expression.getKind() == Kind.Variable) {
                        VariableExpression variable = (VariableExpression) expression;
                        String name = variable.getName();
                        Expression init = variable.getInit();
                        if (init != null) {
                            ISqlExpression context = visit(variable.getInit());
                            // 某些数据库不支持直接返回bool类型，所以需要做一下包装
                            context = boxTheBool(variable.getInit(), context);
                            setAs(expressions, context, name);
                        }
                    }
                }
                return factory.select(expressions, newExpression.getType());
            }
            else {
                return checkAndReturnValue(newExpression);
            }
        }
    }

    @Override
    public ISqlExpression visit(ParameterExpression parameter) {
        if (asNameMap.containsKey(parameter)) {
            Class<?> type = parameter.getType();
            MetaData metaData = MetaDataCache.getMetaData(type);
            //propertyMetaData.addAll(metaData.getColumns().values());
            List<ISqlExpression> expressions = new ArrayList<>();
            for (FieldMetaData pm : metaData.getNotIgnorePropertys()) {
                expressions.add(factory.column(pm, asNameMap.get(parameter)));
            }
            return factory.select(expressions, parameter.getType());
        }
        else {
            throw new SqLinkException(String.format("非lambda内部的ParameterExpression,名称:%s 类型:%s ", parameter.getName(), parameter.getType()));
        }
    }

    @Override
    public ISqlExpression visit(BlockExpression blockExpression) {
        List<ISqlSetExpression> sqlSetExpressions = new ArrayList<>();
        for (Expression expression : blockExpression.getExpressions()) {
            ISqlExpression visit = visit(expression);
            if (visit instanceof ISqlSetExpression) {
                sqlSetExpressions.add((ISqlSetExpression) visit);
            }
            else {
                throw new SqLinkException(String.format("意外的sql表达式类型:%s 表达式为:%s", visit.getClass(), expression));
            }
        }
        ISqlSetsExpression sets = factory.sets();
        sets.addSet(sqlSetExpressions);
        return sets;
    }

    protected ISqlValueExpression checkAndReturnValue(MethodCallExpression expression) {
        Method method = expression.getMethod();
        if (ExpressionUtil.isVoid(method.getReturnType()) || hasParameter(expression)) {
            throw new SqLinkIllegalExpressionException(expression);
        }
        return factory.AnyValue(expression.getValue());
    }

    protected ISqlValueExpression checkAndReturnValue(FieldSelectExpression expression) {
        if (hasParameter(expression)) throw new SqLinkIllegalExpressionException(expression);
        return factory.AnyValue(expression.getValue());
    }

    protected ISqlValueExpression checkAndReturnValue(NewExpression expression) {
        if (hasParameter(expression)) throw new SqLinkIllegalExpressionException(expression);
        return factory.AnyValue(expression.getValue());
    }

    protected boolean hasParameter(Expression expression) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        expression.accept(new DeepFindVisitor() {
            @Override
            public void visit(ParameterExpression parameterExpression) {
                atomicBoolean.set(true);
            }
        });
        return atomicBoolean.get();
    }

    protected SqlExtensionExpression getSqlFuncExt(SqlExtensionExpression[] sqlExtensionExpressions) {
        DbType dbType = config.getDbType();
        Optional<SqlExtensionExpression> first = Arrays.stream(sqlExtensionExpressions).filter(a -> a.dbType() == dbType).findFirst();
        if (!first.isPresent()) {
            Optional<SqlExtensionExpression> any = Arrays.stream(sqlExtensionExpressions).filter(a -> a.dbType() == DbType.Any).findFirst();
            if (any.isPresent()) {
                return any.get();
            }
            throw new SqlFuncExtNotFoundException(dbType);
        }
        else {
            return first.get();
        }
    }

    protected ParamMatcher match(String input) {
        ParamMatcher paramMatcher = new ParamMatcher();

        List<String> bracesContent = paramMatcher.bracesContent;
        List<String> remainder = paramMatcher.remainder;
        // 正则表达式匹配"{}"内的内容
        Pattern pattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = pattern.matcher(input);

        int lastIndex = 0; // 上一个匹配项结束的位置
        while (matcher.find()) {
            // 添加上一个匹配项到剩余字符串（如果有的话）
            if (lastIndex < matcher.start()) {
                remainder.add(input.substring(lastIndex, matcher.start()));
            }

            // 提取并添加"{}"内的内容
            bracesContent.add(matcher.group(1));

            // 更新上一个匹配项结束的位置
            lastIndex = matcher.end();
        }

        // 添加最后一个匹配项之后的剩余字符串（如果有的话）
        if (lastIndex < input.length()) {
            remainder.add(input.substring(lastIndex));
        }

        if (input.startsWith("{")) remainder.add(0, "");
        if (input.endsWith("}")) remainder.add("");

        return paramMatcher;
    }

    protected ISqlQueryableExpression columnToQuery(ISqlColumnExpression columnExpression) {
        // A.B();
        // FROM A WHERE (SELECT ... FROM B WHERE B.ID = (SELECT A.ID FROM A))
        //                                                V
        //                                                V
        // FROM A WHERE (SELECT ... FROM B WHERE B.ID = A.ID)
        FieldMetaData fieldMetaData = columnExpression.getFieldMetaData();
        ISqlRealTableExpression table;
        ISqlConditionsExpression condition = null;
        AsName mainTableAsName = columnExpression.getTableAsName();
        AsName subTableAsName;
        Set<String> stringSet = new HashSet<>();
        stringSet.add(fromExpression.getAsName().getName());
        for (ISqlJoinExpression join : joinsExpression.getJoins()) {
            stringSet.add(join.getAsName().getName());
        }
        for (AsName asName : asNameDeque) {
            stringSet.add(asName.getName());
        }
        //String subTableAsName = doGetAsName(MetaDataCache.getMetaData(getTargetType(fieldMetaData.getGenericType())).getTableName().toLowerCase().substring(0, 1));
        if (fieldMetaData.hasNavigate()) {
            NavigateData navigateData = fieldMetaData.getNavigateData();
            Class<?> targetType = navigateData.getNavigateTargetType();
            table = factory.table(targetType);
            subTableAsName = doGetAsName(getFirst(targetType), stringSet);
            MetaData targetMetaData = MetaDataCache.getMetaData(targetType);
            condition = factory.condition();
            FieldMetaData targetFieldMetaData = targetMetaData.getFieldMetaDataByFieldName(navigateData.getTargetFieldName());
            FieldMetaData selfFieldMetaData = MetaDataCache.getMetaData(fieldMetaData.getParentType()).getFieldMetaDataByFieldName(navigateData.getSelfFieldName());
            condition.addCondition(factory.binary(SqlOperator.EQ, factory.column(targetFieldMetaData, subTableAsName), factory.column(selfFieldMetaData, mainTableAsName)));
        }
        else {
            Type genericType = fieldMetaData.getGenericType();
            table = factory.table(getTargetType(genericType));
            subTableAsName = doGetAsName(getFirst(table.getMainTableClass()), stringSet);
        }
        ISqlQueryableExpression queryable = factory.queryable(factory.from(table, subTableAsName));
        if (condition != null) {
            queryable.addWhere(condition);
        }
        return queryable;
    }

    protected ISqlQueryableExpression queryToQuery(ISqlQueryableExpression left, Method method) {
        // A.B().C();
        // FROM A WHERE (SELECT C.FIELD FROM C WHERE C.ID = (SELECT B.ID FROM B WHERE B.ID = (SELECT A.ID FROM A)))
        // FROM A WHERE (SELECT C.FIELD FROM C WHERE C.ID = (SELECT B.ID FROM B WHERE B.ID = A.ID))
        // 先拿到B表
        MetaData metaData = MetaDataCache.getMetaData(method.getDeclaringClass());
        // 检查B和C对应关系
        FieldMetaData fieldMetaData = metaData.getFieldMetaDataByGetter(method);
        if (!fieldMetaData.hasNavigate()) {
            throw new SqLinkException(String.format("没有在%s.%s上找到与%s的关联关系", method.getDeclaringClass(), fieldMetaData.getProperty(), method.getReturnType()));
        }

        // 为left增加字段选择
        NavigateData navigateData = fieldMetaData.getNavigateData();
        Class<?> targetType = navigateData.getNavigateTargetType();
        FieldMetaData leftFieldDate = metaData.getFieldMetaDataByFieldName(navigateData.getSelfFieldName());
        AsName leftAsName = left.getFrom().getAsName();
        left.setSelect(factory.select(Collections.singletonList(factory.column(leftFieldDate, leftAsName)), targetType));

        // 编写外层sql
        Set<String> stringSet = new HashSet<>();
        stringSet.add(fromExpression.getAsName().getName());
        for (ISqlJoinExpression join : joinsExpression.getJoins()) {
            stringSet.add(join.getAsName().getName());
        }
        for (AsName asName : asNameDeque) {
            stringSet.add(asName.getName());
        }
        AsName targetAs = doGetAsName(getFirst(targetType), stringSet);
        MetaData targetMetaData = MetaDataCache.getMetaData(targetType);
        ISqlQueryableExpression targetQuery = factory.queryable(factory.from(factory.table(targetType), targetAs));
        ISqlConditionsExpression condition = factory.condition();
        targetQuery.addWhere(condition);
        FieldMetaData targetFieldMetaDate = targetMetaData.getFieldMetaDataByFieldName(navigateData.getTargetFieldName());
        condition.addCondition(factory.binary(SqlOperator.EQ, factory.column(targetFieldMetaDate, targetAs), factory.column(leftFieldDate, leftAsName)));

        return targetQuery;
    }

    protected void setAs(List<ISqlExpression> contexts, ISqlExpression expression, String name) {
        if (expression instanceof ISqlColumnExpression) {
            ISqlColumnExpression sqlColumn = (ISqlColumnExpression) expression;
            if (!sqlColumn.getFieldMetaData().getColumn().equals(name)) {
                contexts.add(factory.as(expression, name));
            }
            else {
                contexts.add(expression);
            }
        }
        else {
            contexts.add(factory.as(expression, name));
        }
    }

    protected ISqlExpression boxTheBool(Expression init, ISqlExpression result) {
        if (init instanceof MethodCallExpression) {
            MethodCallExpression methodCall = (MethodCallExpression) init;
            return boxTheBool(isBool(methodCall.getMethod().getReturnType()), result);
        }
        else if (init instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) init;
            return boxTheBool(unary.getOperatorType() == OperatorType.NOT, result);
        }
        return result;
    }

    protected ISqlExpression boxTheBool(boolean condition, ISqlExpression result) {
        if (!condition) return result;
        switch (config.getDbType()) {
            case SQLServer:
            case Oracle:
                return LogicExpression.IfExpression(config, result, factory.constString("1"), factory.constString("0"));
            default:
                return result;
        }
    }

    public ISqlSelectExpression toSelect(LambdaExpression<?> lambda) {
        ISqlExpression expression = visit(lambda);
        ISqlSelectExpression selectExpression;
        if (expression instanceof ISqlSelectExpression) {
            selectExpression = (ISqlSelectExpression) expression;
        }
        else {
            SqlExpressionFactory factory = config.getSqlExpressionFactory();
            // 用于包装某些数据库不支持直接返回bool
            if (isBool(lambda.getReturnType())) {
                switch (config.getDbType()) {
                    case SQLServer:
                    case Oracle:
                        expression = LogicExpression.IfExpression(config, expression, factory.constString("1"), factory.constString("0"));
                }
            }
            selectExpression = factory.select(Collections.singletonList(expression), lambda.getReturnType(), true, false);
        }
        return selectExpression;
    }

    public ISqlGroupByExpression toGroup(LambdaExpression<?> lambda) {
        ISqlExpression expression = visit(lambda);
        ISqlGroupByExpression group;
        if (expression instanceof ISqlGroupByExpression) {
            group = (ISqlGroupByExpression) expression;
        }
        else {
            throw new SqLinkException(String.format("意外的类型:%s 表达式为:%s", expression.getClass(), lambda));
        }
        return group;
    }

    public ISqlColumnExpression toColumn(LambdaExpression<?> lambda) {
        ISqlExpression expression = visit(lambda);
        ISqlColumnExpression column;
        if (expression instanceof ISqlColumnExpression) {
            column = (ISqlColumnExpression) expression;
        }
        else {
            throw new SqLinkException(String.format("意外的类型:%s 表达式为:%s", expression.getClass(), lambda));
        }
        return column;
    }
}
