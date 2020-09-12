package org.noear.solon.extend.validation;

import org.noear.solon.annotation.XNote;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;

import java.lang.annotation.Annotation;

/**
 * 失败打印器
 *
 * @author noear
 * @since 1.0.27
 * */
public interface ValidatorEventHandler {
    /**
     * @return 是否停止后续检查器
     */
    @XNote("@return 是否停止后续检查器")
    boolean onFailure(XContext ctx, Annotation ano, XResult result, String message);
}
