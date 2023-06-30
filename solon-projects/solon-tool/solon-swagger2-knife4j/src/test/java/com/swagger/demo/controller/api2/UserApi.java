package com.swagger.demo.controller.api2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2023/6/30 created
 */
@Api(tags = "用户接口")
@Component
public class UserApi {
    @ApiOperation("添加用户")
    @Mapping("user/add")
    public void userAdd(String name){

    }
}
