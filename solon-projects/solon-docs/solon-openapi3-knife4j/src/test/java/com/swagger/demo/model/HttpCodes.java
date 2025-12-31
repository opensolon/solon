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

import java.util.LinkedHashMap;

public class HttpCodes extends LinkedHashMap<Integer,String> {

    public HttpCodes() {
        super();
        put(200, "请求成功");
        put(400, "服务器不理解请求的语法");
        put(403, "服务器拒绝请求");
        put(404, "服务器找不到请求的网页");
        put(405, "禁用请求中指定的方法");
        put(500, "服务器遇到错误，无法完成请求");
    }
}
