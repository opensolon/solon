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
final class LiteralWithIndexingWriter extends IndexNameValueWriter {

    private boolean tableUpdated;

    private CharSequence name;
    private CharSequence value;
    private int index;

    LiteralWithIndexingWriter() {
        super(0b0100_0000, 6);
    }

    @Override
    LiteralWithIndexingWriter index(int index) {
        super.index(index);
        this.index = index;
        return this;
    }

    @Override
    LiteralWithIndexingWriter name(CharSequence name, boolean useHuffman) {
        super.name(name, useHuffman);
        this.name = name;
        return this;
    }

    @Override
    LiteralWithIndexingWriter value(CharSequence value, boolean useHuffman) {
        super.value(value, useHuffman);
        this.value = value;
        return this;
    }

    @Override
    public boolean write(HeaderTable table, ByteBuffer destination) {
        if (!tableUpdated) {
            CharSequence n;
            if (indexedRepresentation) {
                n = table.get(index).name;
            } else {
                n = name;
            }
            table.put(n, value);
            tableUpdated = true;
        }
        return super.write(table, destination);
    }

    @Override
    public IndexNameValueWriter reset() {
        tableUpdated = false;
        name = null;
        value = null;
        index = -1;
        return super.reset();
    }
}
