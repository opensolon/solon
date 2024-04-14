package com.layjava.docs.javadoc.solon.wrap;

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
