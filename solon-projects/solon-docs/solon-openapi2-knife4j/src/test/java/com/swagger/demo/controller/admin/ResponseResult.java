package com.swagger.demo.controller.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 请求响应实体
 *
 * @author 多仔ヾ
 */
@Data
@NoArgsConstructor
@ApiModel(description= "返回响应数据")
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 **/
    @ApiModelProperty(value = "状态码")
    private Integer code;

    /** 响应信息 **/
    @ApiModelProperty(value = "状态描述")
    private String message;

    /** 响应数据 **/
    @ApiModelProperty(value = "响应数据")
    private T data;

    /** 响应时间 **/
    @ApiModelProperty(value = "响应时间")
    private Long timestamp;

}
