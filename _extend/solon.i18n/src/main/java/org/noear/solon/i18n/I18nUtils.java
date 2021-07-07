package org.noear.solon.i18n;

import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;

import java.util.*;

/**
 * 国际化工具
 *
 * @author noear
 * @since 1.5
 */
public class I18nUtils {
    private static I18nBundleFactory bundleFactory = new I18nBundleFactoryLocal();
    private static LocaleResolver localeResolver = new LocaleResolverOfHeader();

    static {
        Aop.getAsyn(I18nBundleFactory.class, bw -> {
            bundleFactory = bw.raw();
        });

        Aop.getAsyn(LocaleResolver.class, bw -> {
            localeResolver = bw.raw();
        });
    }


    public static I18nBundle get(String bundleName, Locale locale) {
        return bundleFactory.create(bundleName, locale);
    }

    public static I18nBundle get(String bundleName, Context ctx) {
        Locale locale = ctx.getLocale();
        if (locale == null) {
            locale = localeResolver.getLocale(ctx);
        }

        return get(bundleName, locale);
    }
}
