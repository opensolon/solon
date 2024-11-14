/*
 * Copyright 2017-2024 noear.org and authors
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
import org.noear.solon.data.sqlink.api.crud.read.LQuery;
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
import java.util.stream.Stream;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.isGetter;

/**
 * 表达式解析器
 *
 * @author kiryu1223
 * @since 3.0
 */
public abstract class SqlVisitor extends ResultThrowVisitor<ISqlExpression> {
    protected List<ParameterExpression> parameters;
    protected final SqLinkConfig config;
    protected int offset;
    protected final SqlExpressionFactory factory;

    protected SqlVisitor(SqLinkConfig config) {
        this(config, 0);
    }

    protected SqlVisitor(SqLinkConfig config, int offset) {
        this.config = config;
        this.offset = offset;
        this.factory = config.getSqlExpressionFactory();
    }

    /**
     * lambda表达式解析
     */
    @Override
    public ISqlExpression visit(LambdaExpression<?> lambda) {
        if (parameters == null) {
            parameters = lambda.getParameters();
            return visit(lambda.getBody());
        }
        else {
            SqlVisitor self = getSelf(offset);
            return self.visit(lambda);
        }
    }

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
        if (ExpressionUtil.isProperty(parameters, fieldSelect)) {
            ParameterExpression parameter = (ParameterExpression) fieldSelect.getExpr();
            int index = parameters.indexOf(parameter) + offset;
            Field field = fieldSelect.getField();
            MetaData metaData = MetaDataCache.getMetaData(field.getDeclaringClass());
            return factory.column(metaData.getFieldMetaDataByFieldName(field.getName()), index);
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
        // 分组对象的聚合函数
        if (IAggregation.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            String name = methodCall.getMethod().getName();
            List<Expression> args = methodCall.getArgs();
            switch (name) {
                case "count":
                    return AggregateMethods.count(config, args.isEmpty() ? null : visit(args.get(0)));
                case "sum":
                    return AggregateMethods.sum(config, visit(args.get(0)));
                case "avg":
                    return AggregateMethods.avg(config, visit(args.get(0)));
                case "max":
                    return AggregateMethods.max(config, visit(args.get(0)));
                case "min":
                    return AggregateMethods.min(config, visit(args.get(0)));
                case "groupConcat":
                    List<ISqlExpression> visit = new ArrayList<>();
                    for (Expression arg : args) {
                        visit.add(visit(arg));
                    }
                    return AggregateMethods.groupConcat(config, visit);
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
                    return factory.unary(operator, visit(args.get(0)));
                }
                else {
                    ISqlExpression left = visit(methodCall.getArgs().get(0));
                    ISqlExpression right = visit(methodCall.getArgs().get(1));
                    return factory.binary(operator, left, right);
                }
            }
        }
        // 集合的流函数
        else if (Stream.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            if (method.getName().equals("anyMatch")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression cond = visit(methodCall.getArgs().get(0));
                offset--;
                queryable.addWhere(cond);
                queryable.setSelect(factory.select(Collections.singletonList(factory.constString("1")), int.class));
                return factory.unary(SqlOperator.EXISTS, queryable);
            }
            else if (method.getName().equals("noneMatch")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression cond = visit(methodCall.getArgs().get(0));
                offset--;
                queryable.addWhere(cond);
                queryable.setSelect(factory.select(Collections.singletonList(factory.constString("1")), int.class));
                ISqlUnaryExpression exists = factory.unary(SqlOperator.EXISTS, queryable);
                return factory.unary(SqlOperator.NOT, exists);
            }
            else if (method.getName().equals("count")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.count(config, null)), long.class));
                offset--;
                return queryable;
            }
            else if (method.getName().equals("filter")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression cond = visit(methodCall.getArgs().get(0));
                offset--;
                queryable.addWhere(cond);
                return queryable;
            }
            else {
                return checkAndReturnValue(methodCall);
            }
        }
        // 子查询发起者
        else if (SubQuery.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            if (method.getName().equals("subQuery")) {
                ISqlExpression visit = visit(methodCall.getExpr());
                // a.getItems().stream()
                if (visit instanceof ISqlColumnExpression) {
                    ISqlColumnExpression columnExpression = (ISqlColumnExpression) visit;
                    FieldMetaData fieldMetaData = columnExpression.getFieldMetaData();
                    ISqlRealTableExpression table;
                    ISqlConditionsExpression condition = null;
                    //if(!fieldMetaData.hasNavigate()) throw new SqLinkException("包含导航的属性才能使用stream()方法");
                    if (fieldMetaData.hasNavigate()) {
                        NavigateData navigateData = fieldMetaData.getNavigateData();
                        table = factory.table(navigateData.getNavigateTargetType());
                        condition = factory.condition();
                        MetaData metaData = MetaDataCache.getMetaData(navigateData.getNavigateTargetType());
                        FieldMetaData targetFieldMetaData = metaData.getFieldMetaDataByFieldName(navigateData.getTargetFieldName());
                        FieldMetaData selfFieldMetaData = MetaDataCache.getMetaData(fieldMetaData.getParentType()).getFieldMetaDataByFieldName(navigateData.getSelfFieldName());
                        condition.addCondition(factory.binary(SqlOperator.EQ, factory.column(targetFieldMetaData, offset), factory.column(selfFieldMetaData)));
                    }
                    else {
                        // todo:先糊弄一下
                        Type genericType = fieldMetaData.getGenericType();
                        if (genericType instanceof Class) {
                            table = factory.table((Class<?>) genericType);
                        }
                        else {
                            ParameterizedType type = (ParameterizedType) genericType;
                            Type actualTypeArgument = type.getActualTypeArguments()[0];
                            table = factory.table((Class<?>) actualTypeArgument);
                        }
                    }
                    ISqlQueryableExpression queryable = factory.queryable(factory.from(table, offset));
                    if (condition != null) {
                        queryable.addWhere(condition);
                    }
                    return queryable;
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
        else if (LQuery.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass())) {
            Method method = methodCall.getMethod();
            if (method.getName().equals("count")) {
                offset++;
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
                offset--;
                return queryable;
            }
            else if (method.getName().equals("sum")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression column = visit(methodCall.getArgs().get(0));
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.sum(config, column)), BigDecimal.class));
                offset--;
                return queryable;
            }
            else if (method.getName().equals("avg")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression column = visit(methodCall.getArgs().get(0));
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.avg(config, column)), BigDecimal.class));
                offset--;
                return queryable;
            }
            else if (method.getName().equals("min")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression column = visit(methodCall.getArgs().get(0));
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.min(config, column)), BigDecimal.class));
                offset--;
                return queryable;
            }
            else if (method.getName().equals("max")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression column = visit(methodCall.getArgs().get(0));
                queryable.setSelect(factory.select(Collections.singletonList(AggregateMethods.max(config, column)), BigDecimal.class));
                offset--;
                return queryable;
            }
            else if (method.getName().equals("any")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression cond = visit(methodCall.getArgs().get(0));
                offset--;
                queryable.addWhere(cond);
                queryable.setSelect(factory.select(Collections.singletonList(factory.constString("1")), int.class));
                return factory.unary(SqlOperator.EXISTS, queryable);
            }
            else if (method.getName().equals("where")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression cond = visit(methodCall.getArgs().get(0));
                offset--;
                queryable.addWhere(cond);
                return queryable;
            }
            else if (method.getName().equals("select")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                SelectVisitor selectVisitor = new SelectVisitor(config, queryable, offset);
                ISqlExpression select = selectVisitor.visit(methodCall.getArgs().get(0));
                offset--;
                if (select instanceof ISqlSelectExpression) {
                    ISqlSelectExpression iSqlSelectExpression = (ISqlSelectExpression) select;
                    queryable.setSelect(iSqlSelectExpression);
                }
                else {
                    throw new SqLinkException("意外的sql表达式类型:" + select.getClass());
                }
                return factory.queryable(queryable);
            }
            else if (method.getName().equals("distinct")) {
                offset++;
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
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                HavingVisitor havingVisitor = new HavingVisitor(config, queryable, offset);
                List<Expression> args = methodCall.getArgs();
                ISqlExpression orderByColumn = havingVisitor.visit(args.get(0));
                offset--;
                if (args.size() > 1) {
                    ISqlExpression value = havingVisitor.visit(args.get(1));
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
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                HavingVisitor havingVisitor = new HavingVisitor(config, queryable, offset);
                List<Expression> args = methodCall.getArgs();
                ISqlExpression orderByColumn = havingVisitor.visit(args.get(0));
                offset--;
                if (args.size() > 1) {
                    ISqlExpression value = havingVisitor.visit(args.get(1));
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
            else if (method.getName().equals("having")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                ISqlExpression cond = visit(methodCall.getArgs().get(0));
                offset--;
                queryable.addHaving(cond);
                return queryable;
            }
            else if (method.getName().equals("limit")) {
                offset++;
                ISqlExpression visit = visit(methodCall.getExpr());
                if (!(visit instanceof ISqlQueryableExpression)) {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
                ISqlQueryableExpression queryable = (ISqlQueryableExpression) visit;
                offset--;
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
            else if (method.getName().equals("stream")) {
                ISqlExpression visit = visit(methodCall.getExpr());
                // a.getItems().stream()
                if (visit instanceof ISqlColumnExpression) {
                    ISqlColumnExpression columnExpression = (ISqlColumnExpression) visit;
                    FieldMetaData fieldMetaData = columnExpression.getFieldMetaData();
                    ISqlRealTableExpression table;
                    ISqlConditionsExpression condition = null;
                    //if(!fieldMetaData.hasNavigate()) throw new SqLinkException("包含导航的属性才能使用stream()方法");
                    if (fieldMetaData.hasNavigate()) {
                        NavigateData navigateData = fieldMetaData.getNavigateData();
                        table = factory.table(navigateData.getNavigateTargetType());
                        condition = factory.condition();
                        MetaData metaData = MetaDataCache.getMetaData(navigateData.getNavigateTargetType());
                        FieldMetaData targetFieldMetaData = metaData.getFieldMetaDataByFieldName(navigateData.getTargetFieldName());
                        FieldMetaData selfFieldMetaData = MetaDataCache.getMetaData(fieldMetaData.getParentType()).getFieldMetaDataByFieldName(navigateData.getSelfFieldName());
                        condition.addCondition(factory.binary(SqlOperator.EQ, factory.column(targetFieldMetaData, offset), factory.column(selfFieldMetaData)));
                    }
                    else {
                        // todo:先糊弄一下
                        Type genericType = fieldMetaData.getGenericType();
                        if (genericType instanceof Class) {
                            table = factory.table((Class<?>) genericType);
                        }
                        else {
                            ParameterizedType type = (ParameterizedType) genericType;
                            Type actualTypeArgument = type.getActualTypeArguments()[0];
                            table = factory.table((Class<?>) actualTypeArgument);
                        }
                    }
                    ISqlQueryableExpression queryable = factory.queryable(factory.from(table, offset));
                    if (condition != null) {
                        queryable.addWhere(condition);
                    }
                    return queryable;
                }
                else {
                    throw new SqLinkException("不支持的表达式:" + methodCall);
                }
            }
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
        // 字段
        else if (ExpressionUtil.isProperty(parameters, methodCall)) {
            if (isGetter(methodCall.getMethod())) {
                ParameterExpression parameter = (ParameterExpression) methodCall.getExpr();
                int index = parameters.indexOf(parameter) + offset;
                Method getter = methodCall.getMethod();
                MetaData metaData = MetaDataCache.getMetaData(getter.getDeclaringClass());
                return factory.column(metaData.getFieldMetaDataByGetter(getter), index);
            }
            else if (ExpressionUtil.isSetter(methodCall.getMethod())) {
                ParameterExpression parameter = (ParameterExpression) methodCall.getExpr();
                int index = parameters.indexOf(parameter) + offset;
                Method setter = methodCall.getMethod();
                MetaData metaData = MetaDataCache.getMetaData(setter.getDeclaringClass());
                FieldMetaData fieldMetaData = metaData.getFieldMetaDataBySetter(setter);
                ISqlColumnExpression columnExpression = factory.column(fieldMetaData, index);
                ISqlExpression value = visit(methodCall.getArgs().get(0));
                return factory.set(columnExpression, value);
            }
            else {
                return checkAndReturnValue(methodCall);
            }
        }
        // 值
        else {
            return checkAndReturnValue(methodCall);
        }
    }

    /**
     * 二元运算表达式解析
     */
    @Override
    public ISqlExpression visit(BinaryExpression binary) {
        Expression left = binary.getLeft();
        Expression right = binary.getRight();
        return factory.binary(
                SqlOperator.valueOf(binary.getOperatorType().name()),
                visit(left),
                visit(right)
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
        return checkAndReturnValue(newExpression);
    }

    /**
     * 获得一个新的自己
     */
    protected abstract SqlVisitor getSelf();

    /**
     * 获得一个新的自己
     */
    protected abstract SqlVisitor getSelf(int offset);

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
}
