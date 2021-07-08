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
public class I18nUtil {
    private static I18nBundleFactory bundleFactory = new I18nBundleFactoryLocal();
    private static final Map<String, I18nBundle> bundleCached = new HashMap<>();
    private static LocaleResolver localeResolver = new LocaleResolverOfHeader();

    private static final String messageBundleName = "i18n.messages";

    static {
        Aop.getAsyn(I18nBundleFactory.class, bw -> {
            bundleFactory = bw.raw();
        });

        Aop.getAsyn(LocaleResolver.class, bw -> {
            localeResolver = bw.raw();
        });
    }


    /**
     * 获取国际化包
     */
    public static I18nBundle getBundle(String bundleName, Locale locale) {
        String cacheKey = bundleName + "#" + locale.hashCode();

        I18nBundle bundle = bundleCached.get(cacheKey);

        if (bundle == null) {
            synchronized (cacheKey.intern()) {
                bundle = bundleCached.get(cacheKey);

                if (bundle == null) {
                    bundle = bundleFactory.create(bundleName, locale);
                    bundleCached.put(cacheKey, bundle);
                }
            }
        }

        return bundle;
    }

    /**
     * 获取国际化包
     */
    public static I18nBundle getBundle(String bundleName, Context ctx) {
        Locale locale = ctx.getLocale();
        if (locale == null) {
            locale = localeResolver.getLocale(ctx);
        }

        return getBundle(bundleName, locale);
    }


    /**
     * 获取国际化消息
     *
     * @param ctx 上下文
     * @param key 键
     */
    public static String getMessage(Context ctx, String key) {
        Locale locale = ctx.getLocale();
        if (locale == null) {
            locale = localeResolver.getLocale(ctx);
        }

        return getMessage(locale, key, null);
    }

    /**
     * 获取国际化消息
     */
    public static String getMessage(Locale locale, String code) {
        return getMessage(locale, code, null);
    }

    /**
     * 获取国际化消息
     *
     * @param ctx  上下文
     * @param code 代码
     * @param args 格式化参数
     */
    public static String getMessage(Context ctx, String code, Object[] args) {
        Locale locale = ctx.getLocale();
        if (locale == null) {
            locale = localeResolver.getLocale(ctx);
        }

        return getMessage(locale, code, args);
    }

    /**
     * 获取国际化消息
     *
     * @param locale 地区
     * @param code   代码
     * @param args   格式化参数
     */
    public static String getMessage(Locale locale, String code, Object[] args) {
        I18nBundle bundle = getMessageBundle(locale);

        if (args == null || args.length == 0) {
            return bundle.get(code);
        } else {
            return bundle.getAndFormat(code, args);
        }
    }

    /**
     * 获取国际化消息块
     *
     * @param ctx 上下文
     */
    public static I18nBundle getMessageBundle(Context ctx) {
        return getBundle(messageBundleName, ctx);
    }

    /**
     * 获取国际化消息块
     *
     * @param locale 地区
     */
    public static I18nBundle getMessageBundle(Locale locale) {
        return getBundle(messageBundleName, locale);
    }
}
