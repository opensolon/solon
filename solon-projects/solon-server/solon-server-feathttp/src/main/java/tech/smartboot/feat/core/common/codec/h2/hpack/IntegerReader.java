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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.lang.String.format;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
final class IntegerReader {

    private static final int NEW = 0;
    private static final int CONFIGURED = 1;
    private static final int FIRST_BYTE_READ = 2;
    private static final int DONE = 4;

    private int state = NEW;

    private int N;
    private int maxValue;
    private int value;
    private long r;
    private long b = 1;

    public IntegerReader configure(int N) {
        return configure(N, Integer.MAX_VALUE);
    }

    //
    // Why is it important to configure 'maxValue' here. After all we can wait
    // for the integer to be fully read and then check it. Can't we?
    //
    // Two reasons.
    //
    // 1. Value wraps around long won't be unnoticed.
    // 2. It can spit out an exception as soon as it becomes clear there's
    // an overflow. Therefore, no need to wait for the value to be fully read.
    //
    public IntegerReader configure(int N, int maxValue) {
        if (state != NEW) {
            throw new IllegalStateException("Already configured");
        }
        checkPrefix(N);
        if (maxValue < 0) {
            throw new IllegalArgumentException(
                    "maxValue >= 0: maxValue=" + maxValue);
        }
        this.maxValue = maxValue;
        this.N = N;
        state = CONFIGURED;
        return this;
    }

    public boolean read(ByteBuffer input) throws IOException {
        if (state == NEW) {
            throw new IllegalStateException("Configure first");
        }
        if (state == DONE) {
            return true;
        }
        if (!input.hasRemaining()) {
            return false;
        }
        if (state == CONFIGURED) {
            int max = (2 << (N - 1)) - 1;
            int n = input.get() & max;
            if (n != max) {
                value = n;
                state = DONE;
                return true;
            } else {
                r = max;
            }
            state = FIRST_BYTE_READ;
        }
        if (state == FIRST_BYTE_READ) {
            // variable-length quantity (VLQ)
            byte i;
            do {
                if (!input.hasRemaining()) {
                    return false;
                }
                i = input.get();
                long increment = b * (i & 127);
                if (r + increment > maxValue) {
                    throw new IOException(format(
                            "Integer overflow: maxValue=%,d, value=%,d",
                            maxValue, r + increment));
                }
                r += increment;
                b *= 128;
            } while ((128 & i) == 128);

            value = (int) r;
            state = DONE;
            return true;
        }
        throw new InternalError(Arrays.toString(
                new Object[]{state, N, maxValue, value, r, b}));
    }

    public int get() throws IllegalStateException {
        if (state != DONE) {
            throw new IllegalStateException("Has not been fully read yet");
        }
        return value;
    }

    private static void checkPrefix(int N) {
        if (N < 1 || N > 8) {
            throw new IllegalArgumentException("1 <= N <= 8: N= " + N);
        }
    }

    public IntegerReader reset() {
        b = 1;
        state = NEW;
        return this;
    }
}
