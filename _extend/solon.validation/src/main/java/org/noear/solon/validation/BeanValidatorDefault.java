package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

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
     * @param obj 实体对象
     * @param groups 分组（有些实现，可能不支持）
     * @return 验证结果
     * */
    @Override
    public Result validate(Object obj, Class<?>... groups) {
        return ValidatorManager.validateOfEntity(obj);
    }
}
