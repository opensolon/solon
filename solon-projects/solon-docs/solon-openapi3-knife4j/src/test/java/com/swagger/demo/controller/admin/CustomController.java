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

import com.swagger.demo.model.bean.CordonAlarmData;
import com.swagger.demo.model.bean.CordonAlarmResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/16
 */
@Mapping("/customRes")
@Tag(name = "HelloWorld", description = "自定义返回值")
@Controller
public class CustomController {

    @Operation(summary = "新增实验性质的自定义返回值格式", description = "对接强势第三方,需要按对方约定格式返回数据")
    @Parameters({
            @Parameter(name = "paramA", description = "参数a"),
            @Parameter(name = "paramB", description = "参数b"),
    })
    @Mapping("test8")
    public CordonAlarmResult test8(String paramA, String paramB) {
        CordonAlarmData cordonAlarmData = new CordonAlarmData();
        cordonAlarmData.setAction(1);
        cordonAlarmData.setDevId(1);
        cordonAlarmData.setEid(5);
        cordonAlarmData.seteType(24);
        cordonAlarmData.setUpTime(System.currentTimeMillis() / 1000);

        CordonAlarmResult cordonAlarmResult = new CordonAlarmResult();
        cordonAlarmResult.setCode(0);
        cordonAlarmResult.setSysTime(System.currentTimeMillis() / 1000);
        cordonAlarmResult.setData(cordonAlarmData);

        return cordonAlarmResult;
    }
}
