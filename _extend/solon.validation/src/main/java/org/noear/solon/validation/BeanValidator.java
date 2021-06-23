package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

/**
 * 实体验证器接口
 *
 * @author noear
 * @since 1.3
 */
public interface BeanValidator {
    /**
     * 验证
     *
     * @param obj 实体对象
     * @param groups 分组（有些实现，可能不支持）
     * @return 验证结果
     * */
    Result validate(Object obj, Class<?>... groups);
}
