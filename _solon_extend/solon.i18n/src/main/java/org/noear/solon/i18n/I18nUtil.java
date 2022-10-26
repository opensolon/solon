package org.noear.solon.i18n;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.i18n.impl.I18nBundleFactoryLocal;
import org.noear.solon.i18n.impl.LocaleResolverHeader;

import java.util.*;

/**
 * 国际化工具
 *
 * @author noear
 * @since 1.5
 */
public class I18nUtil {
    /**
     * 国际化内容包工厂
     */
    private static I18nBundleFactory bundleFactory = new I18nBundleFactoryLocal();
    /**
     * 国际化内容包缓存
     */
    private static final Map<String, I18nBundle> bundleCached = new HashMap<>();

    /**
     * 地区解析器
     */
    private static LocaleResolver localeResolver = new LocaleResolverHeader();

    /**
     * 获取 地区解析器
     */
    public static LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    /**
     * 消息国际化内容包名
     */
    private static final String messageBundleName = "i18n.messages";

    /**
     * 获取 消息国际化内容包名
     */
    public static String getMessageBundleName() {
        return messageBundleName;
    }

    static {
        Solon.context().getWrapAsync(I18nBundleFactory.class, bw -> {
            bundleFactory = bw.raw();
        });

        Solon.context().getWrapAsync(LocaleResolver.class, bw -> {
            localeResolver = bw.raw();
        });
    }


    /**
     * 获取国际化内容包
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
     * 获取国际化内容包
     */
    public static I18nBundle getBundle(String bundleName, Context ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("The 'ctx' parameter cannot be null");
        }

        Locale locale = ctx.getLocale();
        if (locale == null) {
            locale = localeResolver.getLocale(ctx);
        }

        return getBundle(bundleName, locale);
    }

    /**
     * 获取国际化消息
     *
     * @param code 代码
     */
    public static String getMessage(String code) {
        return getMessage(Context.current(), code, null);
    }

    /**
     * 获取国际化消息
     *
     * @param ctx  上下文
     * @param code 代码
     */
    public static String getMessage(Context ctx, String code) {
        return getMessage(ctx, code, null);
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
     * @param code 代码
     * @param args 格式化参数
     */
    public static String getMessage(String code, Object[] args) {
        return getMessage(Context.current(), code, args);
    }

    /**
     * 获取国际化消息
     *
     * @param ctx  上下文
     * @param code 代码
     * @param args 格式化参数
     */
    public static String getMessage(Context ctx, String code, Object[] args) {
        if (ctx == null) {
            throw new IllegalArgumentException("The 'ctx' parameter cannot be null");
        }

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
     * 获取国际化消息包
     */
    public static I18nBundle getMessageBundle() {
        return getBundle(messageBundleName, Context.current());
    }

    /**
     * 获取国际化消息包
     *
     * @param ctx 上下文
     */
    public static I18nBundle getMessageBundle(Context ctx) {
        return getBundle(messageBundleName, ctx);
    }

    /**
     * 获取国际化消息包
     *
     * @param locale 地区
     */
    public static I18nBundle getMessageBundle(Locale locale) {
        return getBundle(messageBundleName, locale);
    }
}
