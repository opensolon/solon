package org.noear.solon.i18n;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.i18n.util.LocaleUtil;

import java.util.*;

/**
 * 国际化工具
 *
 * @author noear
 * @since 1.5
 */
public class I18nUtils {
    private static I18nBundleFactory bundleFactory = new I18nBundleFactoryLocal();

    public static I18nBundle get(String bundleName, Locale locale) {
        return bundleFactory.create(bundleName, locale);
    }

    public static I18nBundle get(String bundleName, Context ctx) {
        if (ctx.getLocale() == null) {
            String lang = ctx.header("Accept-Language");
            if (Utils.isEmpty(lang)) {
                ctx.setLocale(Locale.getDefault());
            } else {
                ctx.setLocale(LocaleUtil.toLocale(lang));
            }
        }

        return get(bundleName, ctx.getLocale());
    }
}
