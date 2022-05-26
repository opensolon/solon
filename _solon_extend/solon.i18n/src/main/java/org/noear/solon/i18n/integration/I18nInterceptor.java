package org.noear.solon.i18n.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.i18n.I18nBundle;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.i18n.annotation.I18n;

/**
 * 国际化方法拦截器
 *
 * @author noear
 * @since 1.5
 */
public final class I18nInterceptor implements Interceptor {
    public static final I18nInterceptor instance = new I18nInterceptor();

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Object rst = inv.invoke();

        if (rst instanceof ModelAndView) {
            Context ctx = Context.current();
            I18n anno = inv.method().getAnnotation(I18n.class);

            if (anno == null) {
                anno = inv.target().getClass().getAnnotation(I18n.class);
            }

            if (anno != null && ctx != null) {
                ModelAndView mv = (ModelAndView) rst;

                String bundleName = Utils.annoAlias(anno.value(), anno.bundle());
                I18nBundle bundle;

                if (Utils.isEmpty(bundleName)) {
                    bundle = I18nUtil.getMessageBundle(ctx);
                } else {
                    bundle = I18nUtil.getBundle(bundleName, ctx);
                }

                mv.put("i18n", bundle);
            }
        }

        return rst;
    }
}
