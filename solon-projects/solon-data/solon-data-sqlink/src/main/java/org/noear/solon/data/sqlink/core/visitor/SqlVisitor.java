package org.noear.solon.data.sqlink.core.visitor;


import org.noear.solon.data.sqlink.core.api.crud.read.group.IAggregation;
import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.sqlExt.SqlExtensionExpression;
import org.noear.solon.data.sqlink.base.sqlExt.SqlOperatorMethod;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;
import org.noear.solon.data.sqlink.core.visitor.methods.*;
import org.noear.solon.data.sqlink.core.exception.SQLinkException;
import org.noear.solon.data.sqlink.core.exception.IllegalExpressionException;
import org.noear.solon.data.sqlink.core.exception.SqlFuncExtNotFoundException;
import io.github.kiryu1223.expressionTree.expressions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class SqlVisitor extends ResultThrowVisitor<ISqlExpression>
{
    protected List<ParameterExpression> parameters;
    protected final IConfig config;
    protected final int offset;
    protected final SqlExpressionFactory factory;

    protected SqlVisitor(IConfig config)
    {
        this(config, 0);
    }

    public SqlVisitor(IConfig config, int offset)
    {
        this.config = config;
        this.offset = offset;
        this.factory = config.getSqlExpressionFactory();
    }

    @Override
    public ISqlExpression visit(LambdaExpression<?> lambda)
    {
        if (parameters == null)
        {
            parameters = lambda.getParameters();
            return visit(lambda.getBody());
        }
        else
        {
            SqlVisitor self = getSelf();
            return self.visit(lambda);
        }
    }

    @Override
    public ISqlExpression visit(AssignExpression assignExpression)
    {
        ISqlExpression left = visit(assignExpression.getLeft());
        if (left instanceof ISqlColumnExpression)
        {
            ISqlColumnExpression sqlColumnExpression = (ISqlColumnExpression) left;
            ISqlExpression right = visit(assignExpression.getRight());
            return factory.set(sqlColumnExpression, right);
        }
        throw new SQLinkException("表达式中不能出现非lambda入参为赋值对象的语句");
    }

    @Override
    public ISqlExpression visit(FieldSelectExpression fieldSelect)
    {
        if (ExpressionUtil.isProperty(parameters, fieldSelect))
        {
            ParameterExpression parameter = (ParameterExpression) fieldSelect.getExpr();
            int index = parameters.indexOf(parameter) + offset;
            Field field = fieldSelect.getField();
            MetaData metaData = MetaDataCache.getMetaData(field.getDeclaringClass());
            return factory.column(metaData.getPropertyMetaDataByFieldName(field.getName()), index);
        }
        else
        {
            return checkAndReturnValue(fieldSelect);
        }
    }

    @Override
    public ISqlExpression visit(MethodCallExpression methodCall)
    {
        if (IAggregation.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass()))
        {
            String name = methodCall.getMethod().getName();
            switch (name)
            {
                case "count":
                    if (methodCall.getMethod().getParameterCount() == 0)
                    {
                        return factory.template(Collections.singletonList("COUNT(*)"), Collections.emptyList());
                    }
                case "sum":
                case "avg":
                case "max":
                case "min":
                    List<ISqlExpression> args = new ArrayList<>(methodCall.getArgs().size());
                    for (Expression arg : methodCall.getArgs())
                    {
                        args.add(visit(arg));
                    }
                    List<String> strings = new ArrayList<>(args.size() + 1);
                    strings.add(name.toUpperCase() + "(");
                    for (int i = 0; i < args.size() - 1; i++)
                    {
                        strings.add(",");
                    }
                    strings.add(")");
                    return factory.template(strings, args);
                default:
                    throw new IllegalExpressionException(methodCall);
            }
        }
        else if (ExpressionUtil.isSqlExtensionExpressionMethod(methodCall.getMethod()))
        {
            Method sqlFunction = methodCall.getMethod();
            SqlExtensionExpression sqlFuncExt = getSqlFuncExt(sqlFunction.getAnnotationsByType(SqlExtensionExpression.class));
            List<Expression> args = methodCall.getArgs();
            List<ISqlExpression> expressions = new ArrayList<>(args.size());
            if (sqlFuncExt.extension() != BaseSqlExtension.class)
            {
                for (Expression arg : args)
                {
                    expressions.add(visit(arg));
                }
                BaseSqlExtension sqlExtension = BaseSqlExtension.getCache(sqlFuncExt.extension());
                return sqlExtension.parse(config, sqlFunction, expressions);
            }
            else
            {
                List<String> strings = new ArrayList<>();
                List<Parameter> methodParameters = Arrays.stream(methodCall.getMethod().getParameters()).collect(Collectors.toList());
                ParamMatcher match = match(sqlFuncExt.template());
                List<String> functions = match.remainder;
                List<String> params = match.bracesContent;
                for (int i = 0; i < functions.size(); i++)
                {
                    strings.add(functions.get(i));
                    if (i < params.size())
                    {
                        String param = params.get(i);
                        Parameter targetParam = methodParameters.stream()
                                .filter(f -> f.getName().equals(param))
                                .findFirst()
                                .orElseThrow(() -> new SQLinkException("无法在" + sqlFuncExt.template() + "中找到" + param));
                        int index = methodParameters.indexOf(targetParam);

                        // 如果是可变参数
                        if (targetParam.isVarArgs())
                        {
                            while (index < args.size())
                            {
                                expressions.add(visit(args.get(index)));
                                if (index < args.size() - 1) strings.add(sqlFuncExt.separator());
                                index++;
                            }
                        }
                        // 正常情况
                        else
                        {
                            expressions.add(visit(args.get(index)));
                        }
                    }
                }
                return factory.template(strings, expressions);
            }
        }
        else if (List.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass()))
        {
            Method method = methodCall.getMethod();
            if (method.getName().equals("contains"))
            {
                ISqlExpression left = visit(methodCall.getArgs().get(0));
                ISqlExpression right = visit(methodCall.getExpr());

                return factory.binary(SqlOperator.IN, left, right);
            }
            else
            {
                return checkAndReturnValue(methodCall);
            }
        }
        else if (String.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass()))
        {
            Method method = methodCall.getMethod();
            if (Modifier.isStatic(method.getModifiers()))
            {
                switch (method.getName())
                {
                    case "join":
                    {
                        ISqlExpression delimiter = visit(methodCall.getArgs().get(0));
                        //String.join(CharSequence delimiter, CharSequence... elements)
                        if (method.isVarArgs())
                        {
                            List<ISqlExpression> args = new ArrayList<>(methodCall.getArgs().size() - 1);
                            for (int i = 1; i < methodCall.getArgs().size(); i++)
                            {
                                args.add(visit(methodCall.getArgs().get(i)));
                            }
                            return StringMethods.joinArray(config, delimiter, args);
                        }
                        else
                        {
                            ISqlExpression elements = visit(methodCall.getArgs().get(1));
                            return StringMethods.joinList(config, delimiter, elements);
                        }
                    }
                    default:
                        return checkAndReturnValue(methodCall);
                }
            }
            else
            {
                switch (method.getName())
                {
                    case "contains":
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return StringMethods.contains(config, left, right);
                    }
                    case "startsWith":
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return StringMethods.startsWith(config, left, right);
                    }
                    case "endsWith":
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return StringMethods.endsWith(config, left, right);
                    }
                    case "length":
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.length(config, left);
                    }
                    case "toUpperCase":
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.toUpperCase(config, left);
                    }
                    case "toLowerCase":
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.toLowerCase(config, left);
                    }
                    case "concat":
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return StringMethods.concat(config, left, right);
                    }
                    case "trim":
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.trim(config, left);
                    }
                    case "isEmpty":
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        return StringMethods.isEmpty(config, left);
                    }
                    case "indexOf":
                    {
                        if (method.getParameterTypes()[0] == String.class)
                        {
                            ISqlExpression thisStr = visit(methodCall.getExpr());
                            ISqlExpression subStr = visit(methodCall.getArgs().get(0));
                            if (method.getParameterCount() == 1)
                            {
                                return StringMethods.indexOf(config, thisStr, subStr);
                            }
                            else
                            {
                                ISqlExpression fromIndex = visit(methodCall.getArgs().get(1));
                                return StringMethods.indexOf(config, thisStr, subStr, fromIndex);
                            }
                        }
                    }
                    case "replace":
                    {
                        ISqlExpression thisStr = visit(methodCall.getExpr());
                        ISqlExpression oldStr = visit(methodCall.getArgs().get(0));
                        ISqlExpression newStr = visit(methodCall.getArgs().get(1));
                        return StringMethods.replace(config, thisStr, oldStr, newStr);
                    }
                    case "substring":
                    {
                        ISqlExpression thisStr = visit(methodCall.getExpr());
                        ISqlExpression beginIndex = visit(methodCall.getArgs().get(0));
                        if (method.getParameterCount() == 1)
                        {
                            return StringMethods.substring(config, thisStr, beginIndex);
                        }
                        else
                        {
                            ISqlExpression endIndex = visit(methodCall.getArgs().get(1));
                            return StringMethods.substring(config, thisStr, beginIndex, endIndex);
                        }
                    }
                    default:
                        return checkAndReturnValue(methodCall);
                }
            }
        }
        else if (Math.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass()))
        {
            Method method = methodCall.getMethod();
            switch (method.getName())
            {
                case "abs":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("ABS(", ")"), Collections.singletonList(arg));
                }
                case "cos":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("COS(", ")"), Collections.singletonList(arg));
                }
                case "acos":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("ACOS(", ")"), Collections.singletonList(arg));
                }
                case "sin":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("SIN(", ")"), Collections.singletonList(arg));
                }
                case "asin":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("ASIN(", ")"), Collections.singletonList(arg));
                }
                case "tan":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("TAN(", ")"), Collections.singletonList(arg));
                }
                case "atan":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("ATAN(", ")"), Collections.singletonList(arg));
                }
                case "atan2":
                {
                    ISqlExpression arg1 = visit(methodCall.getArgs().get(0));
                    ISqlExpression arg2 = visit(methodCall.getArgs().get(1));
                    return MathMethods.atan2(config, arg1, arg2);
                }
                case "toDegrees":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.toDegrees(config, arg);
                }
                case "toRadians":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.toRadians(config, arg);
                }
                case "exp":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("EXP(", ")"), Collections.singletonList(arg));
                }
                case "floor":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("FLOOR(", ")"), Collections.singletonList(arg));
                }
                case "log":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.log(config, arg);
                }
                case "log10":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.log10(config, arg);
                }
                case "random":
                {
                    return MathMethods.random(config);
                }
                case "round":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return MathMethods.round(config, arg);
                }
                case "pow":
                {
                    ISqlExpression arg1 = visit(methodCall.getArgs().get(0));
                    ISqlExpression arg2 = visit(methodCall.getArgs().get(1));
                    return factory.template(Arrays.asList("POWER(", ",", ")"), Arrays.asList(arg1, arg2));
                }
                case "signum":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("SIGN(", ")"), Collections.singletonList(arg));
                }
                case "sqrt":
                {
                    ISqlExpression arg = visit(methodCall.getArgs().get(0));
                    return factory.template(Arrays.asList("SQRT(", ")"), Collections.singletonList(arg));
                }
                default:
                    return checkAndReturnValue(methodCall);
            }
        }
        else if (BigDecimal.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass()))
        {
            Method method = methodCall.getMethod();
            switch (method.getName())
            {
                case "add":
                {
                    if (method.getParameterCount() == 1)
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return factory.binary(SqlOperator.PLUS, left, right);
                    }
                }
                case "subtract":
                {
                    if (method.getParameterCount() == 1)
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return factory.binary(SqlOperator.MINUS, left, right);
                    }
                }
                case "multiply":
                {
                    if (method.getParameterCount() == 1)
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return factory.binary(SqlOperator.MUL, left, right);
                    }
                }
                case "divide":
                {
                    if (method.getParameterCount() == 1)
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return factory.binary(SqlOperator.DIV, left, right);
                    }
                }
                case "remainder":
                {
                    if (method.getParameterCount() == 1)
                    {
                        ISqlExpression left = visit(methodCall.getExpr());
                        ISqlExpression right = visit(methodCall.getArgs().get(0));
                        return BigDecimalMethods.remainder(config, left, right);
                    }
                }
                default:
                    return checkAndReturnValue(methodCall);
            }
        }
        else if (Temporal.class.isAssignableFrom(methodCall.getMethod().getDeclaringClass()))
        {
            Method method = methodCall.getMethod();
            switch (method.getName())
            {
                case "isAfter":
                {
                    ISqlExpression left = visit(methodCall.getExpr());
                    ISqlExpression right = visit(methodCall.getArgs().get(0));
                    return TemporalMethods.isAfter(config, left, right);
                }
                case "isBefore":
                {
                    ISqlExpression left = visit(methodCall.getExpr());
                    ISqlExpression right = visit(methodCall.getArgs().get(0));
                    return TemporalMethods.isBefore(config, left, right);
                }
                case "isEqual":
                {
                    ISqlExpression left = visit(methodCall.getExpr());
                    ISqlExpression right = visit(methodCall.getArgs().get(0));
                    return TemporalMethods.isEqual(config, left, right);
                }
                default:
                    return checkAndReturnValue(methodCall);
            }
        }
        else if (ExpressionUtil.isSqlOperatorMethod(methodCall.getMethod()))
        {
            Method method = methodCall.getMethod();
            List<Expression> args = methodCall.getArgs();
            SqlOperatorMethod operatorMethod = method.getAnnotation(SqlOperatorMethod.class);
            SqlOperator operator = operatorMethod.value();
            if (operator == SqlOperator.BETWEEN)
            {
                ISqlExpression thiz = visit(args.get(0));
                ISqlExpression min = visit(args.get(1));
                ISqlExpression max = visit(args.get(2));
                return factory.binary(SqlOperator.BETWEEN, thiz, factory.binary(SqlOperator.AND, min, max));
            }
            else
            {
                if (operator.isLeft() || operator == SqlOperator.POSTINC || operator == SqlOperator.POSTDEC)
                {
                    return factory.unary(operator, visit(args.get(0)));
                }
                else
                {
                    ISqlExpression left = visit(methodCall.getArgs().get(0));
                    ISqlExpression right = visit(methodCall.getArgs().get(1));
                    return factory.binary(operator, left, right);
                }
            }
        }
        else if (ExpressionUtil.isProperty(parameters, methodCall))
        {
            if (ExpressionUtil.isGetter(methodCall.getMethod()))
            {
                ParameterExpression parameter = (ParameterExpression) methodCall.getExpr();
                int index = parameters.indexOf(parameter) + offset;
                Method getter = methodCall.getMethod();
                MetaData metaData = MetaDataCache.getMetaData(getter.getDeclaringClass());
                return factory.column(metaData.getPropertyMetaDataByGetter(getter), index);
            }
            else if (ExpressionUtil.isSetter(methodCall.getMethod()))
            {
                ParameterExpression parameter = (ParameterExpression) methodCall.getExpr();
                int index = parameters.indexOf(parameter) + offset;
                Method setter = methodCall.getMethod();
                MetaData metaData = MetaDataCache.getMetaData(setter.getDeclaringClass());
                ISqlColumnExpression columnExpression = factory.column(metaData.getPropertyMetaDataBySetter(setter), index);
                ISqlExpression value = visit(methodCall.getArgs().get(0));
                return factory.set(columnExpression, value);
            }
            else
            {
                return checkAndReturnValue(methodCall);
            }
        }
        else
        {
            return checkAndReturnValue(methodCall);
        }
    }

    @Override
    public ISqlExpression visit(BinaryExpression binary)
    {
        Expression left = binary.getLeft();
        Expression right = binary.getRight();
        return factory.binary(
                SqlOperator.valueOf(binary.getOperatorType().name()),
                visit(left),
                visit(right)
        );
    }

    @Override
    public ISqlExpression visit(UnaryExpression unary)
    {
        return factory.unary(
                SqlOperator.valueOf(unary.getOperatorType().name()),
                visit(unary.getOperand())
        );
    }

    @Override
    public ISqlExpression visit(ConditionalExpression conditional)
    {
        ISqlExpression cond = visit(conditional.getCondition());
        ISqlExpression truePart = visit(conditional.getTruePart());
        ISqlExpression falsePart = visit(conditional.getFalsePart());
        return LogicExpression.IfExpression(config, cond, truePart, falsePart);
    }

    @Override
    public ISqlExpression visit(ParensExpression parens)
    {
        return factory.parens(visit(parens.getExpr()));
    }

    @Override
    public ISqlExpression visit(StaticClassExpression staticClass)
    {
        return factory.type(staticClass.getType());
    }

    @Override
    public ISqlExpression visit(ConstantExpression constant)
    {
        return factory.value(constant.getValue());
    }

    @Override
    public ISqlExpression visit(ReferenceExpression reference)
    {
        return factory.AnyValue(reference.getValue());
    }

    @Override
    public ISqlExpression visit(NewExpression newExpression)
    {
        return checkAndReturnValue(newExpression);
    }

    protected abstract SqlVisitor getSelf();

    protected ISqlValueExpression checkAndReturnValue(MethodCallExpression expression)
    {
        Method method = expression.getMethod();
        if (ExpressionUtil.isVoid(method.getReturnType()) || hasParameter(expression))
        {
            throw new IllegalExpressionException(expression);
        }
        return factory.AnyValue(expression.getValue());
    }

    protected ISqlValueExpression checkAndReturnValue(FieldSelectExpression expression)
    {
        if (hasParameter(expression)) throw new IllegalExpressionException(expression);
        return factory.AnyValue(expression.getValue());
    }

    protected ISqlValueExpression checkAndReturnValue(NewExpression expression)
    {
        if (hasParameter(expression)) throw new IllegalExpressionException(expression);
        return factory.AnyValue(expression.getValue());
    }

    protected boolean hasParameter(Expression expression)
    {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        expression.accept(new DeepFindVisitor()
        {
            @Override
            public void visit(ParameterExpression parameterExpression)
            {
                atomicBoolean.set(true);
            }
        });
        return atomicBoolean.get();
    }

    protected SqlExtensionExpression getSqlFuncExt(SqlExtensionExpression[] sqlExtensionExpressions)
    {
        DbType dbType = config.getDbType();
        Optional<SqlExtensionExpression> first = Arrays.stream(sqlExtensionExpressions).filter(a -> a.dbType() == dbType).findFirst();
        if (!first.isPresent())
        {
            Optional<SqlExtensionExpression> any = Arrays.stream(sqlExtensionExpressions).filter(a -> a.dbType() == DbType.Any).findFirst();
            if (any.isPresent())
            {
                return any.get();
            }
            throw new SqlFuncExtNotFoundException(dbType);
        }
        else
        {
            return first.get();
        }
    }

    protected ParamMatcher match(String input)
    {
        ParamMatcher paramMatcher = new ParamMatcher();

        List<String> bracesContent = paramMatcher.bracesContent;
        List<String> remainder = paramMatcher.remainder;
        // 正则表达式匹配"{}"内的内容
        Pattern pattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = pattern.matcher(input);

        int lastIndex = 0; // 上一个匹配项结束的位置
        while (matcher.find())
        {
            // 添加上一个匹配项到剩余字符串（如果有的话）
            if (lastIndex < matcher.start())
            {
                remainder.add(input.substring(lastIndex, matcher.start()));
            }

            // 提取并添加"{}"内的内容
            bracesContent.add(matcher.group(1));

            // 更新上一个匹配项结束的位置
            lastIndex = matcher.end();
        }

        // 添加最后一个匹配项之后的剩余字符串（如果有的话）
        if (lastIndex < input.length())
        {
            remainder.add(input.substring(lastIndex));
        }

        if (input.startsWith("{")) remainder.add(0, "");
        if (input.endsWith("}")) remainder.add("");

        return paramMatcher;
    }
}
