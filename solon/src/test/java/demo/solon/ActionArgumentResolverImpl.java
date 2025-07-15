package demo.solon;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.ActionArgumentResolver;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.LazyReference;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author noear 2025/7/15 created
 */
@Component
public class ActionArgumentResolverImpl implements ActionArgumentResolver {
    @Override
    public boolean matched(Context ctx, ParamWrap pWrap) {
        return pWrap.getParameter().isAnnotationPresent(Argument.class);
    }

    @Override
    public Object resolveArgument(Context ctx, Object target, MethodWrap mWrap, ParamWrap pWrap, int pIndex, LazyReference bodyRef) throws Throwable {
        Argument anno = pWrap.getParameter().getAnnotation(Argument.class);

        return anno.value();
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Argument {
        String value() default "";
    }
}
