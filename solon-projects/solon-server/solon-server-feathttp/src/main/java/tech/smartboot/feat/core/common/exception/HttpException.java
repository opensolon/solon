/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.exception;

import tech.smartboot.feat.core.common.HttpStatus;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class HttpException extends RuntimeException {
    private final HttpStatus httpStatus;

    public HttpException(HttpStatus httpStatus) {
        this(httpStatus, httpStatus.getReasonPhrase());
    }

    public HttpException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpException(int httpStatusCode, String desc) {
        this(new HttpStatus(httpStatusCode, desc));
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
