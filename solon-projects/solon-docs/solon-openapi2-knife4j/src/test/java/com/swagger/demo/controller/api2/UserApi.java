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
package com.swagger.demo.controller.api2;

import com.swagger.demo.model.Page;
import com.swagger.demo.model.PageImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2023/6/30 created
 */
@Api("用户接口")
@Managed
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
