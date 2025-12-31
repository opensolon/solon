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
package com.swagger.demo.model.dto;

import com.swagger.demo.model.bean.DeviceParamBean;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/10
 */
@Schema(description = "农机具")
public class EquipDto {
    @Schema(description = "农机具id", example = "11")
    private int equipId;

    @Schema(description = "农机具号", example = "2009080002")
    private String equipCode;

    @Schema(description = "农机具号", example = "2009080002")
    private String equipTypeId;

    @Schema(description = "状态 0 正常", example = "0")
    private int status;

    @Schema(description = "作业宽幅", example = "20")
    private String taskWidth;

    @Schema(description = "点位状态阈值上限", example = "40")
    private String pointStateThresholdStart;

    @Schema(description = "点位状态阈值下限", example = "60")
    private String pointStateThresholdEnd;

    @Schema(description = "创建时间", example = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @Schema(description = "创建时间", example = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;

    @Schema(description = "网关附加数据")
    private DeviceParamBean deviceParamBean;
}
