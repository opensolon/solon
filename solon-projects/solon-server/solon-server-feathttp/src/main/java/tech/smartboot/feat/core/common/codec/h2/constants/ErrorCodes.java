/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.codec.h2.constants;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class ErrorCodes {
    public static final int NO_ERROR = 0x0;
    /**
     * The endpoint detected an unspecific protocol error.
     * This error is for use when a more specific error code is not available.
     */
    public static final int PROTOCOL_ERROR = 0x1;
    public static final int INTERNAL_ERROR = 0x2;
    public static final int FLOW_CONTROL_ERROR = 0x3;
    public static final int SETTINGS_TIMEOUT = 0x4;
    public static final int STREAM_CLOSED = 0x5;
    public static final int FRAME_SIZE_ERROR = 0x6;
    public static final int REFUSED_STREAM = 0x7;
    public static final int CANCEL = 0x8;
    public static final int COMPRESSION_ERROR = 0x9;
    public static final int CONNECT_ERROR = 0xa;
    public static final int ENHANCE_YOUR_CALM = 0xb;
    public static final int INADEQUATE_SECURITY = 0xc;
    public static final int HTTP_1_1_REQUIRED = 0xd;
}
