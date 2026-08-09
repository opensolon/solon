/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */
package tech.smartboot.feat.core.common.codec.h2.hpack;

import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
final class IndexedWriter implements BinaryRepresentationWriter {

    private final IntegerWriter intWriter = new IntegerWriter();

    IndexedWriter() {
    }

    IndexedWriter index(int index) {
        intWriter.configure(index, 7, 0b1000_0000);
        return this;
    }

    @Override
    public boolean write(HeaderTable table, ByteBuffer destination) {
        return intWriter.write(destination);
    }

    @Override
    public BinaryRepresentationWriter reset() {
        intWriter.reset();
        return this;
    }
}
