/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.io;

import java.io.IOException;
import java.util.EventListener;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 * @deprecated 适配 Jakarta servlet 规范，未来可能会被清除
 */
public interface WriteListener extends EventListener {


    void onWritePossible() throws IOException;


    void onError(final Throwable t);

}