package org.noear.solon.validation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
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
     * 验证实体
     * */
    default Result validateOfEntity(Object obj, T anno, Field field) {
        return Result.failure();
    }


    /**
     * 验证上下文
     * */
    Result validateOfContext(Context ctx, T anno, String name, StringBuilder tmp);
}
