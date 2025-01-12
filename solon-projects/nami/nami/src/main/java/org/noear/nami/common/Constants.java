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
package org.noear.nami.common;

/**
 * 常量
 *
 * @author noear
 * @since 1.0
 * @deprecated 3.0 {@link ContentTypes}
 * */
@Deprecated
public class Constants {
    public static final String AT_TYPE_JSON = "@type_json";
    public static final String AT_JSON = "@json";
    public static final String AT_PROTOBUF = "@protobuf";
    public static final String AT_HESSIAN = "@hessian";
    public static final String AT_FURY = "@fury";
    public static final String AT_KRYO = "@kryo";
    public static final String AT_ABC = "@abc";

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public static final String HEADER_SERIALIZATION = "X-Serialization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";
}
