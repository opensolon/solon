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
package com.swagger.demo.model.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author: lbq
 * @email: 526509994@qq.com
 * @date: 2021/9/15
 */
@ApiModel(description = "越界报警检测结果")
public class CordonAlarmResult {
    @ApiModelProperty(value = "0:调用成功 -1:调用失败", example = "0")
    private int code;

    @ApiModelProperty(value = "数据")
    private CordonAlarmData data;

    @ApiModelProperty(value = "系统时间", example = "1619934304")
    private long sysTime;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CordonAlarmData getData() {
        return data;
    }

    public void setData(CordonAlarmData data) {
        this.data = data;
    }

    public long getSysTime() {
        return sysTime;
    }

    public void setSysTime(long sysTime) {
        this.sysTime = sysTime;
    }
}
