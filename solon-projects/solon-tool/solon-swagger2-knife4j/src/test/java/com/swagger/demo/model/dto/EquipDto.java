package com.swagger.demo.model.dto;

import com.swagger.demo.model.bean.DeviceParamBean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/10
 */
@ApiModel(description = "农机具")
public class EquipDto {
    @ApiModelProperty(value = "农机具id", example = "11")
    private int equipId;

    @ApiModelProperty(value = "农机具号", example = "2009080002")
    private String equipCode;

    @ApiModelProperty(value = "农机具号", example = "2009080002")
    private String equipTypeId;

    @ApiModelProperty(value = "状态 0 正常", example = "0")
    private int status;

    @ApiModelProperty(value = "作业宽幅", example = "20")
    private String taskWidth;

    @ApiModelProperty(value = "点位状态阈值上限", example = "40")
    private String pointStateThresholdStart;

    @ApiModelProperty(value = "点位状态阈值下限", example = "60")
    private String pointStateThresholdEnd;

    @ApiModelProperty(value = "创建时间", example = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @ApiModelProperty(value = "创建时间", example = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;

    @ApiModelProperty(value = "网关附加数据")
    private DeviceParamBean deviceParamBean;
}
