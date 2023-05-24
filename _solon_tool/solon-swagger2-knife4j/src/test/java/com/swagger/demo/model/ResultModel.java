package com.swagger.demo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 参考AntDesign约定的Json返回格式
 * https://beta-pro.ant.design/docs/request-cn
 *
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/8
 */
@ApiModel(description = "响应结果")
public class ResultModel {
    @ApiModelProperty(value = "请求状态")
    private boolean success;

    @ApiModelProperty(value = "返回数据")
    private Object data;

    @ApiModelProperty(value = "错误码", example = "-1")
    private String errorCode;

    @ApiModelProperty(value = "错误信息", example = "错误信息")
    private String errorMessage;

    @ApiModelProperty(value = "前端错误处理： 0 silent; 1 message.warn; 2 message.error; 4 notification; 9 page",
            example = "2")
    private int showType;

    @ApiModelProperty(value = "方便后端故障排除：唯一请求ID", example = "someid")
    private String traceId;

    @ApiModelProperty(value = "当前访问服务器的主机", example = "127.0.0.1")
    private String host;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
