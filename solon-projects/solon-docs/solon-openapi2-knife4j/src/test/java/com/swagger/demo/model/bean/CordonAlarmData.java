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
