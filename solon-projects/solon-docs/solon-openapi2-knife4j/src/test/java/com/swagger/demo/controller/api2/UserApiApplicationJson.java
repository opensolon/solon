package com.swagger.demo.controller.api2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;

import java.util.Map;

/**
 * @author Sorghum
 * @since 2023/09/22
 */
@Api("用户接口2")
@Component
public class UserApiApplicationJson {
    @ApiOperation(value = "添加用户11")
    @Mapping(value = "user/add111",consumes = "application/json")
    public void userAdd(@ApiParam("用户名") String name){

    }

    @ApiOperation(value = "添加用户22")
    @Mapping(value = "user/add222")
    public void userAdd(@ApiParam("用户名") String name,@Body Map ignore){

    }
}
