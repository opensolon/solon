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
package org.noear.solon.core.util;


import java.io.Serializable;

/**
 * 可抛出的数据，用于数据传导（以实现两种方案：return data; throw data）
 *
 * 在Solon的mvc处理链中，DataThrowable 会做为普通的数据处理渲染
 *
 * <pre>{@code
 * //定义个 DataThrowable 的数据结构
 * public class UapiCode extends DataThrowable{
 *     public final int code;
 *     public final String message;
 *
 *     public UapiCode(int code, String message){
 *         super(message);
 *         this.code = code;
 *         this.message = message;
 *     }
 * }
 *
 * @Managed
 * public class DemoService{
 *     @Inject
 *     UserMapper userMapper;
 *
 *     public UserModel getUser(int userId){
 *         if(userId == 0){
 *             //当与返回类型不同时，可能过 throw 抛出
 *             throw new UapiCode(1,"参数错误");
 *         }
 *
 *         return userMapper.selectById(userId);
 *     }
 * }
 * }</pre>
 *
 * @author noear
 * @since 1.0
 * */
public class DataThrowable extends RuntimeException implements Serializable {

    /**
     * 附带数据
     * */
    private Object data;

    /**
     * 数据获取
     */
    public Object data() {
        return data;
    }

    /**
     * 数据设定
     */
    public DataThrowable data(Object data) {
        this.data = data;
        return this;
    }


    public DataThrowable() {
        super();
    }

    public DataThrowable(Throwable cause) {
        super(cause);
    }

    public DataThrowable(String message) {
        super(message);
    }

    public DataThrowable(String message, Throwable cause) {
        super(message, cause);
    }
}
