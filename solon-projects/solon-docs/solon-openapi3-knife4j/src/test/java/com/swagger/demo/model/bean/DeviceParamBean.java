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


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/2
 */
@Schema(description = "网关附加数据")
public class DeviceParamBean {
    @Schema(description = "车载设备id", example = "1")
    private Integer vobcId;

    @Schema(description = "机具id", example = "2")
    private Integer machineId;

    @Schema(description = "农机状态阈值", example = "20")
    private String machineStateThreshold;

    @Schema(description = "轨迹上报频率", example = "3")
    private String posReportRate;

    public Integer getVobcId() {
        return vobcId;
    }

    public void setVobcId(Integer vobcId) {
        this.vobcId = vobcId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public String getMachineStateThreshold() {
        return machineStateThreshold;
    }

    public void setMachineStateThreshold(String machineStateThreshold) {
        this.machineStateThreshold = machineStateThreshold;
    }

    public String getPosReportRate() {
        return posReportRate;
    }

    public void setPosReportRate(String posReportRate) {
        this.posReportRate = posReportRate;
    }
}
