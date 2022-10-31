package org.noear.solon.i18n;

import org.noear.solon.Utils;

import java.util.Locale;

/**
 * 地区转换工具
 *
 * <pre><code>
 * LocaleUtil.toLocale("zh") == new Locale("zh", "")
 * LocaleUtil.toLocale("zh_Hant") == new Locale("zh", "Hant")
 * LocaleUtil.toLocale("zh_Hant_TW") == new Locale("zh", "Hant", "TW")
 * </code></pre>
 *
 * @author noear
 * @since 1.5
 */
public class LocaleUtil {
    /**
     * 将字符串转换成地区
     *
     * @param lang 语言字符串
     */
    public static Locale toLocale(String lang) {
        return Utils.toLocale(lang);
    }
}
