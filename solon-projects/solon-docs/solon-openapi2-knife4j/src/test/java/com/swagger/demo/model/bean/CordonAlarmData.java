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
@ApiModel(description = "越界报警检测内容")
public class CordonAlarmData {

    public CordonAlarmData() {
        this.eType = 24;
    }

    @ApiModelProperty(value = "围栏ID", example = "5")
    private int eid;

    @ApiModelProperty(value = "终端id", example = "1")
    private int devId;

    @ApiModelProperty(value = "报警类别固定", example = "24")
    private int eType;

    @ApiModelProperty(value = "上报时间", example = "1619934304")
    private long upTime;

    @ApiModelProperty(value = "0:解除报警 1:新报警", example = "1")
    private int action;

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public int geteType() {
        return eType;
    }

    public void seteType(int eType) {
        this.eType = eType;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
