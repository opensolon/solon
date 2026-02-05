package org.noear.solon.shell.annotation;

import java.lang.annotation.*;

/**
 *
 * 标记 Solon Shell 具体命令方法
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Command {
    /**
     * 命令名（终端输入的指令，必填，全局唯一）
     */
    String value();

    /**
     * 命令描述（用于 help 命令展示，可选）
     */
    String description() default "无描述";
}