package org.noear.nami;

import org.noear.nami.common.Result;

/**
 * Nami 执行通道
 *
 * @author noear
 * @since 1.0
 * */
public interface NamiChannel{
    Result call(NamiContext ctx) throws Throwable;
}
