package org.noear.solon.docs.openapi2.wrap;

import io.swagger.annotations.ApiImplicitParam;

/**
 * @author noear
 * @since 2.4
 */
public interface ApiParamAnno extends ApiImplicitParam {
    default boolean hidden() {
        return false;
    }
}
