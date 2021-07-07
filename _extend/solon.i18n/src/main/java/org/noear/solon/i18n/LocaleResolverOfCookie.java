package org.noear.solon.i18n;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

import java.util.Locale;

/**
 * @author noear
 * @since 1.5
 */
public class LocaleResolverOfCookie implements LocaleResolver{
    private String cookieName = "SOLON.LOCALE";

    /**
     * 设置cookie name
     * */
    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    /**
     * 获取地区
     *
     * @param ctx 上下文
     * */
    @Override
    public Locale getLocale(Context ctx) {
        if (ctx.getLocale() == null) {
            String lang = ctx.cookie(cookieName);

            if (Utils.isEmpty(lang)) {
                ctx.setLocale(Locale.getDefault());
            } else {
                ctx.setLocale(LocaleUtil.toLocale(lang));
            }
        }

        return ctx.getLocale();
    }

    /**
     * 设置地区
     *
     * @param ctx 上下文
     * @param locale 地区
     * */
    @Override
    public void setLocale(Context ctx, Locale locale) {
        ctx.cookieSet(cookieName, locale.getLanguage());
        ctx.setLocale(locale);
    }
}
