package org.noear.solon.core.util;

/**
 * 许可证工具
 *
 * @author noear
 * @since 2.7
 */
public class LicenceUtil {
    private static LicenceUtil global;

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        global = ClassUtil.tryInstance("org.noear.solon.extend.impl.LicenceUtilExt");

        if (global == null) {
            global = new LicenceUtil();
        }
    }

    public static LicenceUtil global() {
        return global;
    }

    /**
     * 是否启用
     */
    public boolean isEnable() {
        return false;
    }

    /**
     * 描述
     */
    public String getDescription() {
        return null;
    }
}
