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
