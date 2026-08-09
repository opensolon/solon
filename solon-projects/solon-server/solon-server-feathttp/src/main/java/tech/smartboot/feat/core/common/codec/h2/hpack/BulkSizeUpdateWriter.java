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
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
final class BulkSizeUpdateWriter implements BinaryRepresentationWriter {

    private final SizeUpdateWriter writer = new SizeUpdateWriter();
    private Iterator<Integer> maxSizes;
    private boolean writing;
    private boolean configured;

    BulkSizeUpdateWriter maxHeaderTableSizes(Iterable<Integer> sizes) {
        if (configured) {
            throw new IllegalStateException("Already configured");
        }
        requireNonNull(sizes, "sizes");
        maxSizes = sizes.iterator();
        configured = true;
        return this;
    }

    @Override
    public boolean write(HeaderTable table, ByteBuffer destination) {
        if (!configured) {
            throw new IllegalStateException("Configure first");
        }
        while (true) {
            if (writing) {
                if (!writer.write(table, destination)) {
                    return false;
                }
                writing = false;
            } else if (maxSizes.hasNext()) {
                writing = true;
                writer.reset();
                writer.maxHeaderTableSize(maxSizes.next());
            } else {
                configured = false;
                return true;
            }
        }
    }

    @Override
    public BulkSizeUpdateWriter reset() {
        maxSizes = null;
        writing = false;
        configured = false;
        return this;
    }
}
