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

import com.swagger.demo.model.bean.DeviceParamBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.handle.UploadedFile;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2023/5/25 created
 */
@Mapping("/param")
@Api(description = "参数测试", tags = "示例")
@Controller
public class ParamController {
    @ApiOperation("框架类参数")
    @Mapping("demo1")
    public Result demo1(Context ctx, UploadedFile file) {
        return null;
    }

    @ApiOperation("字符串参数")
    @Mapping("demo2")
    @Get
    public void demo2(String name) {

    }

    @ApiOperation("Header参数")
    @Mapping("demo2-2")
    public void demo2_2(@Header String name) {

    }

    @ApiOperation("Cookie 参数")
    @Mapping("demo2-3")
    public void demo2_3(@Cookie String name, String type) {

    }

    @ApiOperation("Path 参数")
    @Mapping("demo2-4/{name}")
    public void demo2_4(@Path String name, String type) {

    }

    @ApiOperation("Body 字符串参数")
    @Mapping("demo3")
    public void demo3(@Body String body) {

    }

    @ApiOperation("Map 参数")
    @Mapping("demo4")
    public void demo4(@Body Map<String, String> body) {

    }

    @ApiOperation("实体参数")
    @Mapping("demo10")
    public void demo10(DeviceParamBean bean) {

    }

    @ApiOperation("实体列表参数")
    @Post
    @Mapping("demo11")
    public void demo11(@Body List<DeviceParamBean> bean) {

    }

    @ApiOperation("无参数 Post")
    @Post
    @Mapping("demo12")
    public void demo12() {

    }
}
