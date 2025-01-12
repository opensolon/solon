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
package com.swagger.demo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

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

    @ApiModelProperty(value = "错误码", example = "-1", required = true)
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

    @ApiModelProperty(value = "测试long[]")
    private List<Long> ports;

    OrderType orderType;
    UserType userType;

    public OrderType getOrderType() {
        return orderType;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<Long> getPorts() {
        return ports;
    }

    public void setPorts(List<Long> ports) {
        this.ports = ports;
    }

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
