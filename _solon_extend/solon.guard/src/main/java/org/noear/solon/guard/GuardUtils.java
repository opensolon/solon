package org.noear.solon.guard;

import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.guard.impl.AesGuardCoder;

/**
 * 脱敏工具
 *
 * @author noear
 * @since 1.9
 */
public class GuardUtils {
    public static final String TAG_PREFIX = "ENC(";
    public static final String TAG_SUFFIX = ")";

    static GuardCoder guardCoder = new AesGuardCoder();

    static {
        //尝试从容器中获取
        Aop.getAsyn(GuardCoder.class, bw -> {
            guardCoder = bw.get();
        });
    }

    /**
     * 设置编码器
     */
    public static void setGuardCoder(GuardCoder guardCoder) {
        GuardUtils.guardCoder = guardCoder;
    }

    /**
     * 已加密
     */
    public static boolean isEncrypted(String str) {
        return str.startsWith(TAG_PREFIX) && str.endsWith(TAG_SUFFIX);
    }

    /**
     * 加密
     */
    public static String encrypt(String str) {
        if (Utils.isEmpty(str)) {
            return str;
        }


        try {
            String tmp = guardCoder.encrypt(str);

            return TAG_PREFIX + tmp + TAG_SUFFIX;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 解密
     */
    public static String decrypt(String str) {
        if (Utils.isEmpty(str)) {
            return str;
        }


        try {
            if (isEncrypted(str)) {
                str = str.substring(TAG_PREFIX.length(), str.length() - TAG_SUFFIX.length());
                return guardCoder.decrypt(str);
            } else {
                return str;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
