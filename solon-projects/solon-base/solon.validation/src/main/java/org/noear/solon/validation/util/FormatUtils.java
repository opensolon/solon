package org.noear.solon.validation.util;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.8
 */
public class FormatUtils {
    /**
     * 格式化验证消息
     */
    public static String format(Annotation anno, Result rst, String msg) {
        if (Utils.isEmpty(msg)) {
            if (Utils.isEmpty(rst.getDescription())) {
                msg = new StringBuilder(100)
                        .append("@")
                        .append(anno.annotationType().getSimpleName())
                        .append(" verification failed")
                        .toString();
            } else {
                msg = new StringBuilder(100)
                        .append("@")
                        .append(anno.annotationType().getSimpleName())
                        .append(" verification failed: ")
                        .append(rst.getDescription())
                        .toString();
            }
        }

        return msg;
    }
}