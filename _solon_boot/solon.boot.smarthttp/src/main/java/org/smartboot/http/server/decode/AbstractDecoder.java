/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: AbstractDecoder.java
 * Date: 2021-06-10
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.decode;

import org.smartboot.http.common.utils.ByteTree;
import org.smartboot.http.common.utils.Constant;
import org.smartboot.http.server.HttpServerConfiguration;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2021/6/10
 */
public abstract class AbstractDecoder implements Decoder {
    protected static final ByteTree.EndMatcher CR_END_MATCHER = endByte -> endByte == Constant.CR;
    protected static final ByteTree.EndMatcher SP_END_MATCHER = endByte -> endByte == Constant.SP;
    private final HttpServerConfiguration configuration;

    public AbstractDecoder(HttpServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public HttpServerConfiguration getConfiguration() {
        return configuration;
    }
}
