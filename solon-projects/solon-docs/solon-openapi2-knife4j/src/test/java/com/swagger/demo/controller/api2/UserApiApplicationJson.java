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

import com.swagger.demo.model.bean.RoleReq;
import io.swagger.annotations.*;
import org.noear.solon.annotation.*;

import java.util.Map;

/**
 * @author Sorghum
 * @since 2023/09/22
 */
@Api("用户接口2")
@Component
public class UserApiApplicationJson {
    @ApiOperation(value = "添加用户11")
    @Mapping(value = "user/add111", consumes = "application/json")
    public void userAdd(@ApiParam("用户名") String name) {

    }

    @ApiOperation(value = "添加用户22")
    @Mapping(value = "user/add222")
    public void userAdd(@ApiParam("用户名") String name, @Body Map ignore) {

    }

    @ApiOperation(value = "添加用户33")
    @Mapping(value = "user/${path}/add333")
    @Post
    @Consumes("application/json")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "roleName", value = "角色名称",dataType = "String",required = true, paramType = "body")
    )
    public void userAddPath(@ApiParam("帅气路径") @Path String path,@ApiParam("query用户名") String queryName,@Body RoleReq roleReq){
        //RoleReq 中有 roleId和name
    }
}
