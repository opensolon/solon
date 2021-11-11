package org.noear.solon.i18n.integration;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.i18n.annotation.I18n;

/**
 * @author noear
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanAroundAdd(I18n.class, I18nInterceptor.instance);

        app.filter(-9, (ctx, chain) -> {
            //尝试自动完成地区解析
            I18nUtil.getLocaleResolver().getLocale(ctx);
            chain.doFilter(ctx);
        });
    }
}
