package com.swagger.demo.controller.restapi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

@Valid
@Controller
@Mapping("user")
@Api("用户接口")
public class UserController {

    @Post
    @Mapping
    @ApiOperation("添加用户")
    public Result<Long> add(@Validated @Body UserAddDTO dto) {
        return Result.succeed(1L);
    }

    @Put
    @Mapping
    @ApiOperation("编辑用户")
    public Result<Void> edit(@Validated @Body UserEditDTO dto) {
        return Result.succeed();
    }
}