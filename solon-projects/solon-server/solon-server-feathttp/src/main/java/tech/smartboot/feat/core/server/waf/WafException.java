/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.waf;

import tech.smartboot.feat.core.common.HttpStatus;
import tech.smartboot.feat.core.common.exception.HttpException;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class WafException extends HttpException {
    public WafException(HttpStatus httpStatus) {
        super(httpStatus.value(), WafOptions.DESC);
    }

    public WafException(HttpStatus httpStatus, String desc) {
        super(httpStatus.value(), desc);
    }
}
