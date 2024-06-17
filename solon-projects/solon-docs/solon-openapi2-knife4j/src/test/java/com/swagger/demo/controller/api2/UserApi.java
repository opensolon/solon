package com.swagger.demo.controller.api2;

import com.swagger.demo.model.Page;
import com.swagger.demo.model.PageImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2023/6/30 created
 */
@Api("用户接口")
@Component
public class UserApi {
    @ApiOperation("添加用户")
    @Mapping("user/add")
    public void userAdd(@ApiParam("用户名") String name) {

    }

    @ApiOperation("获取用户分页")
    @Mapping("user/page")
    public Page<String> userPage(@ApiParam("用户名") String name) {
        return new PageImpl<>(1, 1);
    }
}
