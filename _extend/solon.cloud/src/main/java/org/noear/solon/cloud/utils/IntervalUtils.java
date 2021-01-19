package org.noear.solon.cloud.utils;

import org.noear.solon.Utils;

/**
 * 时间间隔转换工具
 *
 * @author 夜の孤城
 * @since 1.2
 */
public class IntervalUtils {
    /**
     * 获取简隔时间
     * */
    public static int getInterval(String val) {
        if (Utils.isEmpty(val)) {
            return 0;
        }

        if (val.endsWith("ms")) {
            return Integer.parseInt(val.substring(0, val.length() - 2));
        }

        if (val.endsWith("s")) {
            return Integer.parseInt(val.substring(0, val.length() - 1)) * 1000;
        }

        if (val.indexOf("s") < 0) {
            //ms
            return Integer.parseInt(val);
        }

        return 0;
    }
}
