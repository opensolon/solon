/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.openapi3.base.test.controller;

import demo.openapi3.base.test.domain.Page;
import demo.openapi3.base.test.domain.PageImpl;
import demo.openapi3.base.test.domain.PageResult;
import demo.openapi3.base.test.domain.PageResultImpl;
import demo.openapi3.base.test.domain.bo.UserBo;
import demo.openapi3.base.test.domain.vo.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息表 控制器
 *
 * @author chengliang4810
 * @since 2024-04-08
 */
@Tag(name = "用户信息表 控制器")
@Controller
@Mapping("/test/user")
public class UserController {
    @Operation(summary = "查询用户列表")
    @Get
    @Mapping("/list")
    public List<UserVo> list(UserBo userBo) {
        return null;
    }

    @Operation(summary = "page")
    @Get
    @Mapping("/page")
    public Page page() {
        return new PageImpl(1, 1);
    }

    @Operation(summary = "pageResult")
    @Get
    @Mapping("/pageResult")
    public PageResult<UserVo> pageResult() {
        ArrayList<UserVo> userVos = new ArrayList<>();
        return new PageResultImpl<>(1, userVos);
    }

    @Operation(summary = "pageResultImpl")
    @Get
    @Mapping("/pageResultImpl")
    public PageResultImpl<UserVo> pageResultImpl() {
        ArrayList<UserVo> userVos = new ArrayList<>();
        return new PageResultImpl<>(1, userVos);
    }

    @Operation(summary = "根据Id查询用户信息")
    @Get
    @Mapping("/{id}")
    public UserVo get(Long id) {
        return new UserVo();
    }

    @Operation(summary = "根据Id查询用户信息2")
    @Get
    @Mapping("/info/{id}")
    public Result<UserVo> info(Long id) {
        return Result.succeed(new UserVo());
    }

    @Operation(summary = "新增用户信息")
    @Post
    @Mapping
    public boolean save(UserBo userBo) {
        return true;
    }

    @Operation(summary = "根据Id更新用户信息")
    @Put
    @Mapping
    public boolean update(UserBo userBo) {
        return true;
    }

    @Operation(summary = "删除用户信息")
    @Delete
    @Mapping("/{ids}")
    public boolean delete(String ids) {
        return true;
    }

}