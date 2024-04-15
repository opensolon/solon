package com.layjava.docs.javadoc.solon.controller;

import com.layjava.docs.javadoc.solon.OpenApi2Utils;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

import java.io.IOException;

/**
 * 开放式api控制器
 *
 * @author chengliang
 * @since 2024/04/11
 */
public class OpenApiController {

    @Mapping("swagger/v2")
    public String api(Context ctx, String group) throws IOException {
        return OpenApi2Utils.getApiJson(ctx, group);
    }

}
