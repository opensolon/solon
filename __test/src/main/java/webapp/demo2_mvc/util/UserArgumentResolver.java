package webapp.demo2_mvc.util;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Managed;
import org.noear.solon.core.handle.ActionArgumentResolver;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.LazyReference;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

/**
 * @author noear 2025/7/15 created
 */
@Managed
public class UserArgumentResolver implements ActionArgumentResolver {
    @Override
    public boolean matched(Context ctx, ParamWrap pWrap) {
        return pWrap.getAnnotation(UserAnno.class) != null;
    }

    @Override
    public Object resolveArgument(Context ctx, Object target, MethodWrap mWrap, ParamWrap pWrap, int pIndex, LazyReference bodyRef) throws Throwable {
        UserAnno anno = pWrap.getAnnotation(UserAnno.class);

        return new User(anno.value());
    }
}
