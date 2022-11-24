package org.noear.solon.boot.web;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * 跳转工具
 *
 * @author noear
 * @since 1.11
 */
public class RedirectUtils {
    /**
     * 获取跳转地址
     */
    public static String getRedirectPath(String location) {
        if (Utils.isEmpty(Solon.cfg().serverContextPath())) {
            return location;
        }

        if (location.startsWith("/")) {
            if (location.startsWith(Solon.cfg().serverContextPath())) {
                return location;
            } else {
                return Solon.cfg().serverContextPath() + location.substring(1);
            }
        } else {
            return location;
        }
    }
}
