package org.noear.solon.i18n;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.i18n.util.LocaleUtil;

import java.util.Locale;

/**
 * @author noear
 * @since 1.5
 */
public class LocaleResolverOfHeader implements LocaleResolver {
    private String headerName = "Accept-Language";

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public Locale getLocale(Context ctx) {
        if (ctx.getLocale() == null) {
            String lang = ctx.header(headerName);

            if (Utils.isEmpty(lang)) {
                ctx.setLocale(Locale.getDefault());
            } else {
                ctx.setLocale(LocaleUtil.toLocale(lang));
            }
        }

        return ctx.getLocale();
    }

    @Override
    public void setLocale(Context ctx, Locale locale) {
        ctx.headerSet(headerName, locale.getLanguage());
        ctx.setLocale(locale);
    }
}
