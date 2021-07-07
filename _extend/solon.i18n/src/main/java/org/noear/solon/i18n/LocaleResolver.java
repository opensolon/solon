package org.noear.solon.i18n;

import org.noear.solon.core.handle.Context;

import java.util.Locale;

/**
 * @author noear
 * @since 1.5
 */
public interface LocaleResolver {
    /**
     * 获取地区
     * */
    Locale getLocale(Context ctx);

    /**
     * 设置地区
     * */
    void setLocale(Context ctx, Locale locale);
}
