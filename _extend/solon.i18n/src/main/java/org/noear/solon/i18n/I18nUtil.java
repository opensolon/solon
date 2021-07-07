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
    private static LocaleResolver localeResolver = new LocaleResolverOfHeader();

    private static final String messageBundleName  = "i18n.message";
    private static final Map<Locale, I18nBundle> messageBundleCached = new HashMap<>();

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
        return bundleFactory.create(bundleName, locale);
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
    public static String getMessage(Locale locale, String key) {
        return getMessage(locale, key, null);
    }

    /**
     * 获取国际化消息
     *
     * @param ctx 上下文
     * @param key 键
     * @param args 格式化参数
     */
    public static String getMessage(Context ctx, String key,  Object[] args) {
        Locale locale = ctx.getLocale();
        if (locale == null) {
            locale = localeResolver.getLocale(ctx);
        }

        return getMessage(locale, key, args);
    }

    /**
     * 获取国际化消息
     *
     * @param locale 地区
     * @param key 键
     * @param args 格式化参数
     */
    public static String getMessage(Locale locale, String key, Object[] args) {
        I18nBundle bundle = getMessageBundle(locale);

        if (args == null || args.length == 0) {
            return bundle.get(key);
        } else {
            return bundle.getAndFormat(key, args);
        }
    }

    /**
     * 获取国际化消息块
     *
     * @param locale 地区
     */
    public static I18nBundle getMessageBundle(Locale locale){
        if (locale == null) {
            locale = Locale.getDefault();
        }

        I18nBundle bundle = messageBundleCached.get(locale);
        if (bundle == null) {
            bundle = getBundle(messageBundleName, locale);
            messageBundleCached.put(locale, bundle);
        }

        return bundle;
    }
}
