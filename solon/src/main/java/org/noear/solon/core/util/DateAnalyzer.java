package org.noear.solon.core.util;


import java.text.ParseException;
import java.util.Date;

/**
 * 日期分析器
 *
 * @author noear
 * @since 1.5
 */
public class DateAnalyzer {
    //
    // 可以进行替换扩展
    //
    private static DateAnalyzer global = new DateAnalyzer();

    /**
     * @deprecated 2.3
     */
    @Deprecated
    public static DateAnalyzer getGlobal() {
        return global();
    }

    public static DateAnalyzer global() {
        return global;
    }

    public static void globalSet(DateAnalyzer instance) {
        if (instance != null) {
            global = instance;
        }
    }

    /**
     * 解析
     */
    public Date parse(String val) throws ParseException {
        return DateUtil.parse(val);
    }
}