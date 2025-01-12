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
package org.noear.solon.docs;

/**
 * 接口枚举常量
 *
 * @author noear
 * @since 2.2
 */
public class ApiEnum {
    // httpMethod
    public static final String METHOD_GET = "get";
    public static final String METHOD_POST = "post";
    public static final String METHOD_HEAD = "head";
    public static final String METHOD_PUT = "put";
    public static final String METHOD_DELETE = "delete";
    public static final String METHOD_OPTIONS = "options";
    public static final String METHOD_PATCH = "patch";

    // 可用协议(未参与解析使用)
    public static final String SCHEMES_HTTP = "http";
    public static final String SCHEMES_HTTPS = "https";
    public static final String SCHEMES_WS = "ws";
    public static final String SCHEMES_WSS = "wss";

    // 请求格式
    public static final String CONSUMES_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONSUMES_JSON = "application/json";
    public static final String CONSUMES_XML = "application/xml";
    public static final String CONSUMES_FORM_DATA = "multipart/form-data";
    public static final String CONSUMES_FILE = "application/octet-stream";

    // 返回格式
    public static final String PRODUCES_DEFAULT = "*/*";
    public static final String PRODUCES_JSON = "application/json";
    public static final String PRODUCES_XML = "application/xml";
    public static final String PRODUCES_FILE = "application/octet-stream";
    public static final String PRODUCES_PNG = "image/png";
    public static final String PRODUCES_JPG = "image/jpg";
    public static final String PRODUCES_JPEG = "image/jpeg";
    public static final String PRODUCES_GIF = "image/gif";

    // 参数提交方式
    public static final String PARAM_TYPE_QUERY = "query";
    public static final String PARAM_TYPE_BODY = "body";
    public static final String PARAM_TYPE_HEADER = "header";
    public static final String PARAM_TYPE_COOKIE = "cookie";
    public static final String PARAM_TYPE_PATH = "path";

    // 参数类型
    public static final String STRING = "String";
    public static final String INTEGER = "Integer";
    public static final String LONG = "Long";
    public static final String DOUBLE = "Double";
    public static final String FILE = "file";

    // 自定义格式-请求
    public static final String FORMAT_INTEGER = "int32";
    public static final String FORMAT_LONG = "int64";
    public static final String FORMAT_FLOAT = "float";
    public static final String FORMAT_DOUBLE = "double";
    public static final String FORMAT_STRING = "";
    public static final String FORMAT_BYTE = "byte";
    public static final String FORMAT_BINARY = "binary";
    public static final String FORMAT_BOOLEAN = "boolean";
    public static final String FORMAT_DATE = "date";
    public static final String FORMAT_DATE_TIME = "dateTime";
    public static final String FORMAT_PASSWORD = "password";

    // 自定义格式-返回值
    public static final String RES_STRING = "string";
    public static final String RES_INTEGER = "integer";
    public static final String RES_BOOLEAN = "boolean";
    public static final String RES_OBJECT = "object";
    public static final String RES_ARRAY = "array";
}
