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
package com.swagger.demo.controller.admin;

import com.swagger.demo.model.OrderType;
import com.swagger.demo.model.ResultModel;
import com.swagger.demo.model.ResultModelEx;
import io.swagger.solon.annotation.ApiRes;
import io.swagger.solon.annotation.ApiResProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.docs.ApiEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/16
 */
@Mapping("/simple")
@Tag(name = "HelloWorld", description = "最简用法")
@Controller
public class SimpleController {

    @Operation(summary = "不描述返回值", description = "SwaggerConst.COMMON_RES定义返回")
    @Parameters({
            @Parameter(name = "paramA", description = "参数a", required = true),
            @Parameter(name = "paramB", description = "参数b", required = false )
    })
    @Mapping("test1")
    public Map test1() {
        return new HashMap();
    }


    @Operation(summary = "简单返回值", description = "SwaggerConst.COMMON_RES.data中返回值")
    @Parameters({
            @Parameter(name = "paramA", description = "参数a"),
            @Parameter(name = "paramB", description = "参数b"),
    })
    @ApiRes({
            @ApiResProperty(name = "resA", value = "返回值A", example = "hello word1"),
            @ApiResProperty(name = "resB", value = "返回值b", example = "hello word2"),
    })
    @Mapping("test2")
    public Map test2() {
        return new HashMap();
    }

    @Operation(summary = "url参数Json", description = "多个参数在url中提交,json参数支持多个")
    @Parameters({
            @Parameter(name = "paramA", description = "参数a"),
            @Parameter(name = "paramB", description = "参数b"),
    })
    @ApiRes({
            @ApiResProperty(name = "resA", value = "返回值A", example = "hello word1"),
            @ApiResProperty(name = "resB", value = "返回值b", example = "hello word2"),
    })
    @Put
    @Mapping("test3_put")
    public Map test3_put(String paramA, String paramB, String device) {
        return new HashMap();
    }

    @Operation(summary = "body接口参数Json", description = "在body中提交JSON.注意body只支持一个json串. body提交多个json将失效", method = ApiEnum.METHOD_POST)
    @ApiRes({
            @ApiResProperty(name = "resA", value = "返回值A", example = "hello word1"),
            @ApiResProperty(name = "resB", value = "返回值b", example = "hello word2"),
    })
    @Mapping("test4/${paramPath}")
    public Map test4(@Param(defaultValue = "") Date paramA,
                     String paramB,
                     OrderType paramC,
                     @Path Date paramPath,
                     String device) {
        return new HashMap();
    }

    @Operation(summary = "参数Object嵌套Object", description = "递归实现")
    @ApiRes({
            @ApiResProperty(name = "resA", value = "返回值A", example = "hello word1"),
            @ApiResProperty(name = "resB", value = "返回值b", example = "hello word2"),
    })
    @Mapping("test5")
    public Map test5(String paramA, String paramB, String device) {
        return new HashMap();
    }


    @Operation(summary = "test62")
    @Mapping("test62")
    public ResultModel test6_2(ResultModel model) {
        return model;
    }

    @Operation(summary = "test62-2")
    @Mapping("test62-2")
    public ResultModelEx test6_2_2(ResultModelEx model) {
        return model;
    }

    @Operation(summary = "test63")
    @Mapping("test63")
    public ResultModel test6_3(@Body ResultModel model) {
        return model;
    }

    @Operation(summary = "test63-2")
    @Mapping("test63-2")
    public ResultModelEx test6_3_2(@Body ResultModelEx model) {
        return model;
    }

    @Operation(summary = "url数组参数Json", description = "数组参数在url中提交,json参数支持多个")

    @ApiRes({
            @ApiResProperty(name = "resA", value = "返回值A", example = "hello word1"),
            @ApiResProperty(name = "resB", value = "返回值b", example = "hello word2"),
    })
    @Mapping("test7")
    public Map test7(String paramA, String paramB, String device) {
        return new HashMap();
    }

    @Operation(summary = "body接口数组参数Json", description = "在body中提交JSON.注意body只支持一个json串. body提交多个json将失效", method = ApiEnum.METHOD_POST)
    @ApiRes({
            @ApiResProperty(name = "resA", value = "返回值A", example = "hello word1"),
            @ApiResProperty(name = "resB", value = "返回值b", example = "hello word2"),
    })
    @Mapping("test8")
    public Map test8(String paramA, String paramB, String device) {
        return new HashMap();
    }


    @Operation(summary = "示例2", method = ApiEnum.METHOD_POST)
    @Mapping("upFile")
    public Map upFile() {
        return new HashMap();
    }

    @Operation(summary = "示例2-2")
    @Mapping("upFile2")
    public Map upFile2(UploadedFile file) {
        return new HashMap();
    }
}
