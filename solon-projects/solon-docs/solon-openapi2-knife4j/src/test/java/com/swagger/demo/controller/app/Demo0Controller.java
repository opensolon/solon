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
package com.swagger.demo.controller.app;


import com.swagger.demo.model.bean.RoleReq;
import io.swagger.annotations.*;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import io.swagger.solon.annotation.ApiRes;
import io.swagger.solon.annotation.ApiResProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: lbq
 * @email: 526509994@qq.com
 * @date: 2020/12/21
 */
@Mapping("/demo0")
@Api("0级Path导致UI渲染白屏")
@Controller
public class Demo0Controller {

    @ApiOperation(value = "简单返回值", notes = "SwaggerConst.COMMON_RES.data中返回值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramA", value = "参数a", defaultValue = "1111"),
            @ApiImplicitParam(name = "paramB", value = "参数b", defaultValue = "222"),
            @ApiImplicitParam(name = "headerA", value = "头a", defaultValue = "333", paramType = "header"),
    })
    @ApiRes({
            @ApiResProperty(name = "resA", value = "返回值A", example = "hello word1"),
            @ApiResProperty(name = "resB", value = "返回值b", example = "hello word2"),
    })
    @Mapping("test1")
    public Map test1() {
        return new HashMap();
    }


    @ApiOperation(value = "ApiImplicitParam 测试", notes = "body test")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, paramType = "body"),
            @ApiImplicitParam(name = "menuIds", value = "菜单id数组", required = true, paramType = "body", dataTypeClass = Integer.class, allowMultiple = true),
    })
    @Mapping("test2")
    public Map test2(@ApiParam(hidden = true) @Body RoleReq req) {
        return new HashMap();
    }
}
