/*
 * Copyright 2017-2024 noear.org and authors
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
package com.swagger.demo.controller.app2;

import com.swagger.demo.model.bean.DeviceParamBean;
import com.swagger.demo.model.bean.User;
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
@Mapping("/app/rst")
@Api(description = "结果测试", tags = "示例")
@Controller
public class RstController {
    @ApiOperation("框架类参数与结果")
    @Mapping("demo1")
    public Result<User> demo1(@Body User user) {
        return null;
    }

}
