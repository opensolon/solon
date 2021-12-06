package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

import java.util.Iterator;

/**
 * Bean 验证默认实现
 *
 * @author noear
 * @since 1.5
 */
class BeanValidatorDefault implements BeanValidator {
    /**
     * 验证
     *
     * @param obj    实体对象
     * @param groups 分组（有些实现，可能不支持）
     * @return 验证结果
     */
    @Override
    public Result validate(Object obj, Class<?>... groups) {
        if (obj instanceof Iterable) {
            Iterator iterator = ((Iterable) obj).iterator();
            while (iterator.hasNext()) {
                Object val2 = iterator.next();

                if (val2 != null) {
                    Result rst = ValidatorManager.validateOfEntity(val2);

                    if (rst.getCode() != Result.SUCCEED_CODE) {
                        return rst;
                    }
                }
            }

            return Result.succeed();
        } else {
            return ValidatorManager.validateOfEntity(obj);
        }
    }
}
