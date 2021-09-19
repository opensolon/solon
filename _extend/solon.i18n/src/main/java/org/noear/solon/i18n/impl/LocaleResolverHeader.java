package org.noear.solon.i18n.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.i18n.LocaleResolver;
import org.noear.solon.i18n.LocaleUtil;

import java.util.Locale;

/**
 * 地区解析器，基于 header 处理
 *
 * @author noear
 * @since 1.5
 */
public class LocaleResolverHeader implements LocaleResolver {
    private String headerName1;
    private static final String headerName2 = "Content-Language";
    private static final String headerName3 = "Accept-Language";

    /**
     * 设置header name
     */
    public void setHeaderName(String headerName) {
        this.headerName1 = headerName;
    }

    /**
     * 获取地区
     *
     * @param ctx 上下文
     */
    @Override
    public Locale getLocale(Context ctx) {
        if (ctx.getLocale() == null) {
            String lang = null;

            if (Utils.isEmpty(headerName1)) {
                lang = ctx.header(headerName1);
            }

            if (Utils.isEmpty(lang)) {
                lang = ctx.header(headerName2);
            }

            if (Utils.isEmpty(lang)) {
                lang = ctx.header(headerName3);
            }

            if (Utils.isEmpty(lang)) {
                ctx.setLocale(Locale.getDefault());
            } else {
                if (lang.contains(",")) {
                    lang = lang.split(",")[0];
                }

                ctx.setLocale(LocaleUtil.toLocale(lang));
            }
        }

        return ctx.getLocale();
    }

    /**
     * 设置地区
     *
     * @param ctx    上下文
     * @param locale 地区
     */
    @Override
    public void setLocale(Context ctx, Locale locale) {
        if (Utils.isEmpty(headerName1) == false) {
            ctx.headerSet(headerName1, locale.getLanguage());
            ctx.setLocale(locale);
        }
    }
}
