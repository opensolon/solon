package com.swagger.demo.controller.api2;

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
    public void userAdd(@ApiParam("用户名") String name){

    }
}
