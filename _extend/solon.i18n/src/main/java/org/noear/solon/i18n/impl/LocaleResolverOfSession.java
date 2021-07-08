package org.noear.solon.i18n.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.i18n.LocaleResolver;

import java.util.Locale;

/**
 * 地区解析器，基于 session state 处理
 *
 * @author noear
 * @since 1.5
 */
public class LocaleResolverOfSession implements LocaleResolver {
    private String attrName = "SOLON.LOCALE";

    /**
     * 设置会话属性名
     * */
    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    /**
     * 获取地区
     *
     * @param ctx 上下文
     * */
    @Override
    public Locale getLocale(Context ctx) {
        if (ctx.getLocale() == null) {
            String lang = ctx.session(attrName, "");

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
        ctx.sessionSet(attrName, locale.getLanguage());
        ctx.setLocale(locale);
    }
}
