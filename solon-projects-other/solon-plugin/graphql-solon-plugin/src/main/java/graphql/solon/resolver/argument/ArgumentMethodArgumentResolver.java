package graphql.solon.resolver.argument;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.beanutils.ConvertUtils;
import org.noear.solon.annotation.Param;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.wrap.ParamWrap;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class ArgumentMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Method method,
            ParamWrap paramWrap) {
        Parameter parameter = paramWrap.getParameter();
        Param declaredAnnotation = parameter.getDeclaredAnnotation(Param.class);
        return Objects.nonNull(declaredAnnotation);
    }

    @Override
    public Object resolveArgument(DataFetchingEnvironment environment, Method method,
            ParamWrap[] paramWraps, int index,
            ParamWrap paramWrap) throws Exception {
        GraphQLContext graphQlContext = environment.getGraphQlContext();
        Context ctx = (Context) graphQlContext.get(Context.class);
        return this.buildArgs(environment, ctx, paramWrap);
    }

    /**
     * 构建执行参数
     *
     * @param ctx 上下文
     */
    protected Object buildArgs(DataFetchingEnvironment environment, Context ctx,
            ParamWrap paramWrap) throws IOException {

        String name = paramWrap.getName();
        Object argumentRowValue = environment.getArgument(name);

        Class<?> argumentTragetType = paramWrap.getType();

        Object tv = null;
        if (argumentTragetType.isInstance(argumentRowValue)) {
            tv = argumentRowValue;
        } else if (Context.class.isAssignableFrom(argumentTragetType)) {
            //如果是 Context 类型，直接加入参数
            //
            tv = ctx;
        } else if (ModelAndView.class.isAssignableFrom(argumentTragetType)) {
            //如果是 ModelAndView 类型，直接加入参数
            //
            tv = new ModelAndView();
        } else if (Locale.class.isAssignableFrom(argumentTragetType)) {
            //如果是 Locale 类型，直接加入参数
            //
            tv = ctx.getLocale();
        } else if (UploadedFile.class == argumentTragetType) {
            //如果是 UploadedFile
            //
            tv = ctx.file(paramWrap.getName());
        } else if (argumentTragetType.isInstance(
                ctx.request())) { //getTypeName().equals("javax.servlet.http.HttpServletRequest")
            tv = ctx.request();
        } else if (argumentTragetType.isInstance(
                ctx.response())) { //getTypeName().equals("javax.servlet.http.HttpServletResponse")
            tv = ctx.response();
        } else {
            if (paramWrap.isRequiredBody()) {
                //需要 body 数据
                if (String.class.equals(argumentTragetType)) {
                    tv = ctx.bodyNew();
                } else if (InputStream.class.equals(argumentTragetType)) {
                    tv = ctx.bodyAsStream();
                } else if (Map.class.equals(argumentTragetType)) {
                    tv = environment.getArguments();
                }
            }

            if (tv == null) {
                //尝试数据转换
                try {
                    tv = ConvertUtils.convert(argumentRowValue, argumentTragetType);
                } catch (Exception e) {
                    throw new IllegalArgumentException("convert argument failed!", e);
                }
            }

            if (tv == null) {
                //
                // 如果是基类类型（int,long...），则抛出异常
                //
                if (argumentTragetType.isPrimitive()) {
                    //如果是基本类型，则为给个默认值
                    //
                    if (argumentTragetType == short.class) {
                        tv = (short) 0;
                    } else if (argumentTragetType == int.class) {
                        tv = 0;
                    } else if (argumentTragetType == long.class) {
                        tv = 0L;
                    } else if (argumentTragetType == double.class) {
                        tv = 0d;
                    } else if (argumentTragetType == float.class) {
                        tv = 0f;
                    } else if (argumentTragetType == boolean.class) {
                        tv = false;
                    } else {
                        //
                        //其它类型不支持
                        //
                        throw new IllegalArgumentException(
                                "Please enter a valid parameter @" + paramWrap.getName());
                    }
                }
            }

            if (tv == null) {
                if (paramWrap.isRequiredInput()) {
                    ctx.status(400);
                    throw new IllegalArgumentException(paramWrap.getRequiredHint());
                }
            }

        }
        return tv;
    }
}
