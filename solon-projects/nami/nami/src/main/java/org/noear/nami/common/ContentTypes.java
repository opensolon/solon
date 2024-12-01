/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.nami.common;

/**
 * 内容类型
 *
 * @author noear
 * @since 2.0
 */
public class ContentTypes {
    //仅 Content-Type 值

    public static final String HESSIAN_VALUE = "application/hessian";
    public static final String FURY_VALUE = "application/fury";
    public static final String KRYO_VALUE = "application/kryo";
    public static final String PROTOBUF_VALUE = "application/protobuf";
    public static final String ABC_VALUE = "application/abc";
    public static final String JSON_VALUE = "application/json";
    public static final String JSON_TYPE_VALUE = "application/json-type";
    public static final String FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

    //完整的 Content-Type 申明

    public static final String HESSIAN = "Content-Type=application/hessian";
    public static final String FURY = "Content-Type=application/fury";
    public static final String PROTOBUF = "Content-Type=application/protobuf";
    public static final String ABC = "Content-Type=application/abc";
    public static final String JSON = "Content-Type=application/json";
    public static final String JSON_TYPE = "Content-Type=application/json-type";


    public static final String HESSIAN_ACCEPT = "Accept=application/hessian";
    public static final String FURY_ACCEPT = "Accept=application/fury";
    public static final String PROTOBUF_ACCEPT = "Accept=application/protobuf";
    public static final String ABC_ACCEPT = "Accept=application/abc";
    public static final String JSON_ACCEPT = "Accept=application/json";
}
