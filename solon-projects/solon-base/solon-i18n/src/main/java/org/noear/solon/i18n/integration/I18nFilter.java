package org.noear.solon.i18n.integration;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.i18n.I18nUtil;

/**
 * @author noear
 * @since 3.0
 */
public class I18nFilter implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        //尝试自动完成地区解析
        I18nUtil.getLocaleResolver().getLocale(ctx);
        chain.doFilter(ctx);
    }
}
