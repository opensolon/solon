package org.noear.solon.extend.validation;

import org.noear.solon.core.handle.Result;

/**
 * @author noear
 * @since 1.3
 */
public interface BeanValidator {
    Result validate(Object object, Class<?>... groups);
}
