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
abstract class IndexNameValueWriter implements BinaryRepresentationWriter {

    private final int pattern;
    private final int prefix;
    private final IntegerWriter intWriter = new IntegerWriter();
    private final StringWriter nameWriter = new StringWriter();
    private final StringWriter valueWriter = new StringWriter();

    protected boolean indexedRepresentation;

    private static final int NEW = 0;
    private static final int NAME_PART_WRITTEN = 1;
    private static final int VALUE_WRITTEN = 2;

    private int state = NEW;

    protected IndexNameValueWriter(int pattern, int prefix) {
        this.pattern = pattern;
        this.prefix = prefix;
    }

    IndexNameValueWriter index(int index) {
        indexedRepresentation = true;
        intWriter.configure(index, prefix, pattern);
        return this;
    }

    IndexNameValueWriter name(CharSequence name, boolean useHuffman) {
        indexedRepresentation = false;
        intWriter.configure(0, prefix, pattern);
        nameWriter.configure(name, useHuffman);
        return this;
    }

    IndexNameValueWriter value(CharSequence value, boolean useHuffman) {
        valueWriter.configure(value, useHuffman);
        return this;
    }

    @Override
    public boolean write(HeaderTable table, ByteBuffer destination) {
        if (state < NAME_PART_WRITTEN) {
            if (indexedRepresentation) {
                if (!intWriter.write(destination)) {
                    return false;
                }
            } else {
                if (!intWriter.write(destination) ||
                        !nameWriter.write(destination)) {
                    return false;
                }
            }
            state = NAME_PART_WRITTEN;
        }
        if (state < VALUE_WRITTEN) {
            if (!valueWriter.write(destination)) {
                return false;
            }
            state = VALUE_WRITTEN;
        }
        return state == VALUE_WRITTEN;
    }

    @Override
    public IndexNameValueWriter reset() {
        intWriter.reset();
        if (!indexedRepresentation) {
            nameWriter.reset();
        }
        valueWriter.reset();
        state = NEW;
        return this;
    }
}
