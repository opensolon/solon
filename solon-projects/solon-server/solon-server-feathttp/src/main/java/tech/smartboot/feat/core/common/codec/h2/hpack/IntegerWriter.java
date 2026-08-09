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
import java.util.Arrays;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
final class IntegerWriter {

    private static final int NEW = 0;
    private static final int CONFIGURED = 1;
    private static final int FIRST_BYTE_WRITTEN = 2;
    private static final int DONE = 4;

    private int state = NEW;

    private int payload;
    private int N;
    private int value;

    //
    //      0   1   2   3   4   5   6   7
    //    +---+---+---+---+---+---+---+---+
    //    |   |   |   |   |   |   |   |   |
    //    +---+---+---+-------------------+
    //    |<--------->|<----------------->|
    //       payload           N=5
    //
    // payload is the contents of the left-hand side part of the octet;
    //         it is truncated to fit into 8-N bits, where 1 <= N <= 8;
    //
    public IntegerWriter configure(int value, int N, int payload) {
        if (state != NEW) {
            throw new IllegalStateException("Already configured");
        }
        if (value < 0) {
            throw new IllegalArgumentException("value >= 0: value=" + value);
        }
        checkPrefix(N);
        this.value = value;
        this.N = N;
        this.payload = payload & 0xFF & (0xFFFFFFFF << N);
        state = CONFIGURED;
        return this;
    }

    public boolean write(ByteBuffer output) {
        if (state == NEW) {
            throw new IllegalStateException("Configure first");
        }
        if (state == DONE) {
            return true;
        }

        if (!output.hasRemaining()) {
            return false;
        }
        if (state == CONFIGURED) {
            int max = (2 << (N - 1)) - 1;
            if (value < max) {
                output.put((byte) (payload | value));
                state = DONE;
                return true;
            }
            output.put((byte) (payload | max));
            value -= max;
            state = FIRST_BYTE_WRITTEN;
        }
        if (state == FIRST_BYTE_WRITTEN) {
            while (value >= 128 && output.hasRemaining()) {
                output.put((byte) ((value & 127) + 128));
                value /= 128;
            }
            if (!output.hasRemaining()) {
                return false;
            }
            output.put((byte) value);
            state = DONE;
            return true;
        }
        throw new InternalError(Arrays.toString(
                new Object[]{state, payload, N, value}));
    }

    private static void checkPrefix(int N) {
        if (N < 1 || N > 8) {
            throw new IllegalArgumentException("1 <= N <= 8: N= " + N);
        }
    }

    public IntegerWriter reset() {
        state = NEW;
        return this;
    }
}
