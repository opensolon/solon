package org.noear.solon.validation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * 验证器（对验证注解的支持）
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface Validator<T extends Annotation> {
    default String message(T anno) {
        return "";
    }

    /**
     * 验证实体字段
     *
     * @param clz 实体类
     * @param anno 验证注解
     * @param name 字段名
     * @param val 字段值
     * @param tmp 临时字符构建器（用于构建message；起到复用之效）
     * @return  验证结果
     */
    default Result validateOfEntity(Class<?> clz, T anno, String name, Object val, StringBuilder tmp) {
        return Result.failure();
    }


    /**
     * 验证上下文参数
     *
     * @param ctx 上下文
     * @param anno 验证注解
     * @param name 参数名
     * @param tmp 临时字符构建器（用于构建message；起到复用之效）
     * @return 验证结果
     */
    Result validateOfContext(Context ctx, T anno, String name, StringBuilder tmp);
}
