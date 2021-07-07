package org.noear.solon.i18n;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.i18n.util.LocaleUtil;

import java.util.Locale;

/**
 * @author noear
 * @since 1.5
 */
public class LocaleResolverOfParam implements LocaleResolver {
    private String paramName = "SOLON.LOCALE";

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public Locale getLocale(Context ctx) {
        if (ctx.getLocale() == null) {
            String lang = ctx.param(paramName);

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
        ctx.setLocale(locale);
    }
}
