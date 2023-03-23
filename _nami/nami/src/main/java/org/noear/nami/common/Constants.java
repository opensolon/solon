package org.noear.nami.common;

/**
 * 常量
 *
 * @author noear
 * @since 1.0
 * */
public class Constants {
    @Deprecated
    public static final String CONTENT_TYPE_HESSIAN = "application/hessian";
    @Deprecated
    public static final String CONTENT_TYPE_PROTOBUF = "application/protobuf";
    @Deprecated
    public static final String CONTENT_TYPE_JSON = "application/json";
    @Deprecated
    public static final String CONTENT_TYPE_JSON_TYPE = "application/json-type";
    @Deprecated
    public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static final String AT_TYPE_JSON = "@type_json";
    public static final String AT_PROTOBUF = "@protobuf";
    public static final String AT_HESSIAN = "@hessian";

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public static final String HEADER_SERIALIZATION = "X-Serialization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";
}
