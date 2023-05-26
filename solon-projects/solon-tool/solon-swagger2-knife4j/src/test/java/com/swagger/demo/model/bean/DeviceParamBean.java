package com.swagger.demo.model.bean;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/2
 */
@ApiModel(description = "网关附加数据")
public class DeviceParamBean {
    @ApiModelProperty(value = "车载设备id", example = "1")
    private Integer vobcId;

    @ApiModelProperty(value = "机具id", example = "2")
    private Integer machineId;

    @ApiModelProperty(value = "农机状态阈值", example = "20")
    private String machineStateThreshold;

    @ApiModelProperty(value = "轨迹上报频率", example = "3")
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
