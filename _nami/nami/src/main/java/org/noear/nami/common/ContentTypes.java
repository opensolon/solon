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
    public static final String PROTOBUF_VALUE = "application/protobuf";
    public static final String JSON_VALUE = "application/json";
    public static final String JSON_TYPE_VALUE = "application/json-type";
    public static final String FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

    //完整的 Content-Type 申明

    public static final String HESSIAN = "Content-Type=application/hessian";
    public static final String PROTOBUF = "Content-Type=application/protobuf";
    public static final String JSON = "Content-Type=application/json";
    public static final String JSON_TYPE = "Content-Type=application/json-type";
}
